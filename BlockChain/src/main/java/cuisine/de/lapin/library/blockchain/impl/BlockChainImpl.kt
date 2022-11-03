package cuisine.de.lapin.library.blockchain.impl

import android.util.Log
import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.library.blockchain.utils.createBlock
import cuisine.de.lapin.library.blockchain.utils.getPayload
import cuisine.de.lapin.library.blockchain.utils.sha256
import cuisine.de.lapin.library.blockchain.utils.toBlock
import cuisine.de.lapin.library.blockchain.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

internal class BlockChainImpl(
    private val defaultDifficulty: UInt = DEFAULT_DIFFICULTY,
    private val timeStamp: Long = System.currentTimeMillis(),
) : BlockChain {
    companion object {
        const val DEFAULT_DIFFICULTY = 2u
        private const val GENESIS_EVENT = "GenesisEvent"
        private const val BLOCK_INTERVAL = 300 // 5 minutes
        private const val ALLOWED_RANGE = 120 // 2 minutes
        private const val DIFFICULTY_INTERVAL = 5 // check block per 5 blocks
        private const val THREAD_NAME = "BLOCKCHAIN_THREAD"
    }

    private val _blocks = HashMap<String, String>()
    private var _lastestBlockHash: String = ""
    private var _height: UInt = 0u
    private var difficulty = defaultDifficulty
    private val coroutineContext = newSingleThreadContext(THREAD_NAME)

    private var onUpdateChain: ((Map<String, String>) -> Unit)? = null

    init {
        CoroutineScope(coroutineContext).launch {
            withContext(coroutineContext) {
                addBlock(GENESIS_EVENT, timeStamp)
            }
        }
    }

    override suspend fun addBlock(content: Any, timeStamp: Long) {
        withContext(coroutineContext) {
            createBlock(content, _lastestBlockHash, ++_height, timeStamp, calculateDifficulty())
                .let { block ->
                    _height = block.height
                    _lastestBlockHash = block.hash
                    _blocks[block.hash] = block.toJson()
                    onUpdateChain?.invoke(_blocks)
                }
        }
    }

    private fun calculateDifficulty(): UInt {
        val currentBlock = getBlock(_lastestBlockHash)

        if (currentBlock == null || _height == 0u) {
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
        var currentBlockHash: String? = _lastestBlockHash
        var currentHeight: UInt = _height
        while (true) {
            val block = _blocks[currentBlockHash]?.toBlock() ?: break
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
        return _blocks[hash]?.toBlock()
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == getPayload(this).sha256()
        }
    }

    override fun setOnUpdateChainListener(onUpdateChain: (Map<String, String>) -> Unit) {
        this.onUpdateChain = onUpdateChain
    }
}