package cuisine.de.lapin.library.blockchain.impl

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.library.blockchain.utils.createBlock
import cuisine.de.lapin.library.blockchain.utils.getPayload
import cuisine.de.lapin.library.blockchain.utils.sha256
import cuisine.de.lapin.library.blockchain.utils.toBlock
import cuisine.de.lapin.library.blockchain.utils.toJson
import jetbrains.exodus.bindings.StringBinding.entryToString
import jetbrains.exodus.bindings.StringBinding.stringToEntry
import jetbrains.exodus.env.ContextualEnvironment
import jetbrains.exodus.env.Environments.newContextualInstance
import jetbrains.exodus.env.StoreConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext


internal class BlockChainImpl(
    databasePath: String,
    private val defaultDifficulty: UInt = DEFAULT_DIFFICULTY,
    private val timeStamp: Long = System.currentTimeMillis(),
    private val useDifficulty: Boolean = true
) : BlockChain {
    companion object {
        const val DEFAULT_DIFFICULTY = 2u
        private const val GENESIS_EVENT = "GenesisEvent"
        private const val BLOCK_INTERVAL = 300 // 5 minutes
        private const val ALLOWED_RANGE = 120 // 2 minutes
        private const val DIFFICULTY_INTERVAL = 5 // check block per 5 blocks
        private const val THREAD_NAME = "BLOCKCHAIN_THREAD"
        private const val DATABASE_FILE_NAME = "/.leparesseux"
        private const val STORE_NAME = "LeParesseux"
        private const val STORE_CHAIN_INFO = "ChainInfo"
        private const val LASTEST_BLOCK_CHAIN = "lastestBlockChain"
        private const val BLOCK_HEIGHT = "blockHeight"
    }

    private val coroutineContext = newSingleThreadContext(THREAD_NAME)
    private val env: ContextualEnvironment = newContextualInstance("$databasePath$DATABASE_FILE_NAME")
    private val store = env.openStore(STORE_NAME, StoreConfig.WITHOUT_DUPLICATES)
    private val chainInfo = env.openStore(STORE_CHAIN_INFO, StoreConfig.WITHOUT_DUPLICATES)
    private val _lastestBlockHash: String
        get() {
            env.beginTransaction()
            return chainInfo.get(stringToEntry(LASTEST_BLOCK_CHAIN))?.let { entryToString(it) }
                ?: ""
        }
    private val _height: UInt
        get() {
            env.beginTransaction()
            return chainInfo.get(stringToEntry(BLOCK_HEIGHT))?.let { entryToString(it).toUInt() } ?: 0u
        }
    private var difficulty = defaultDifficulty

    init {
        CoroutineScope(coroutineContext).launch {
            if (_height > 0u) return@launch
            withContext(coroutineContext) {
                addBlock(GENESIS_EVENT, timeStamp)
            }
        }
    }

    override suspend fun addBlock(content: Any, timeStamp: Long) {
        withContext(coroutineContext) {
            createBlock(content, _lastestBlockHash, _height + 1u, timeStamp, calculateDifficulty())
                .let { block ->
                    val txn = env.beginTransaction()
                    store.put(txn, stringToEntry(block.hash), stringToEntry(block.toJson()))
                    chainInfo.put(txn, stringToEntry(LASTEST_BLOCK_CHAIN), stringToEntry(block.hash))
                    chainInfo.put(txn, stringToEntry(BLOCK_HEIGHT), stringToEntry(block.height.toString()))
                    txn.commit()
                }
        }
    }

    private fun calculateDifficulty(): UInt {
        val currentBlock = getBlock(_lastestBlockHash)

        if (useDifficulty.not() || currentBlock == null || _height == 0u) {
            return defaultDifficulty
        }

        var targetBlock = getBlock(_lastestBlockHash)

        for (i in 0 until DIFFICULTY_INTERVAL - 1) {
            targetBlock = getBlock(targetBlock?.previousHash ?: "")
        }

        if (targetBlock == null) {
            return difficulty
        }

        val intervalTime = targetBlock.timeStamp - currentBlock.timeStamp

        return when {
            intervalTime < BLOCK_INTERVAL.minus(ALLOWED_RANGE) -> difficulty.plus(1u) // 지정시간보다 짧게 걸리면 난이도 늘이기
            intervalTime > BLOCK_INTERVAL.plus(ALLOWED_RANGE) -> { // 지정시간보다 길어지면 난이도 줄이기, 다만 초기 난이도값보다는 적게
                val calculatedDifficulty = difficulty.minus(1u)
                if (calculatedDifficulty < defaultDifficulty) {
                    defaultDifficulty
                } else {
                    calculatedDifficulty
                }
            }
            else -> difficulty
        }

    }

    override suspend fun isValidChain(): Boolean = withContext(coroutineContext) {
        var currentBlockHash: String = _lastestBlockHash
        var currentHeight: UInt = _height
        val txn = env.beginTransaction()
        while (true) {
            val entryBlock = store.get(stringToEntry(currentBlockHash)) ?: break
            val block = entryToString(entryBlock).toBlock() ?: break
            if (--currentHeight == 0u) {
                return@withContext true
            }

            currentBlockHash = block.previousHash
        }
        return@withContext false
    }

    override suspend fun getBlockChainAsList(): List<Block> = withContext(coroutineContext) {
        val resultArray = ArrayList<Block>()

        var currentBlockHash: String = _lastestBlockHash
        while (true) {
            //prevHash를 받아서 조회를 했는데, 블록이 없는경우는 끊어진 블록이라 유효하지 않은 블록으로 간주함 - 이건 어떻게 처리할지는 고민 필요.
            val block = getBlock(currentBlockHash) ?: return@withContext emptyList()
            resultArray.add(block)
            if (block.previousHash.isEmpty()) { // prevHash가 없으면 genesis block.
                break
            }

            currentBlockHash = block.previousHash
        }

        return@withContext resultArray
    }

    override fun getBlock(hash: String): Block? {
        env.beginTransaction()
        return store.get(stringToEntry(hash))?.let { entryToString(it) }?.toBlock()
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == getPayload(this).sha256()
        }
    }

    override fun close() {
        env.close()
    }
}