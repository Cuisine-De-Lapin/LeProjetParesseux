package cuisine.de.lapin.library.blockchain.impl

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.library.blockchain.utils.createBlock
import cuisine.de.lapin.library.blockchain.utils.getPayload
import cuisine.de.lapin.library.blockchain.utils.sha256
import cuisine.de.lapin.library.blockchain.utils.toBlock
import cuisine.de.lapin.library.blockchain.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class BlockChainImpl(
    private var difficulty: Int,
    private val timeStamp: Long = System.currentTimeMillis(),
    threadName: String? = null
) : BlockChain {
    companion object {
        private const val GENESIS_EVENT = "GenesisEvent"
    }

    private val _blocks = HashMap<String, String>()
    private var _lastestBlockHash: String = ""
    private var _height: UInt = 0u
    private val coroutineContext = threadName ?.let { newSingleThreadContext(threadName) }  ?: Dispatchers.IO // newSingleThreadContext을 쓰려고 했는데, 쓰레드를 close하기가 마땅치 않아서 runblocking으로 변경

    val blocks: Map<String, String> = _blocks
    val lastestBlockHash
        get() = _lastestBlockHash
    val height
        get() = _height

    var onUpdateChain: ((Map<String, String>) -> Unit)? = null

    init {
        CoroutineScope(coroutineContext).launch {
            runBlocking(coroutineContext) {
                addBlock(GENESIS_EVENT, timeStamp)
            }
        }
    }

    override suspend fun addBlock(content: Any, timeStamp: Long) {
        runBlocking(coroutineContext) {
            createBlock(content, _lastestBlockHash, ++_height, timeStamp, difficulty)
                .let { block ->
                    _height = block.height
                    _lastestBlockHash = block.hash
                    _blocks[block.hash] = block.toJson()
                    onUpdateChain?.invoke(_blocks)
                }
        }
    }

    override fun changeDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }

    override suspend fun isValidChain(): Boolean = runBlocking(coroutineContext) {
        var currentBlockHash: String? = _lastestBlockHash
        var currentHeight: UInt = _height
        while (true) {
            val block = _blocks[currentBlockHash]?.toBlock() ?: break
            if (--currentHeight == 0u) {
                return@runBlocking true
            }

            currentBlockHash = block.previousHash
        }

        return@runBlocking false
    }

    override suspend fun getBlockChainAsList(): List<Block> = runBlocking(coroutineContext) {
        val resultArray = ArrayList<Block>()

        var currentBlockHash: String = _lastestBlockHash
        while (true) {
            //prevHash를 받아서 조회를 했는데, 블록이 없는경우는 끊어진 블록이라 유효하지 않은 블록으로 간주함 - 이건 어떻게 처리할지는 고민 필요.
            val block = getBlock(currentBlockHash) ?: return@runBlocking emptyList()
            resultArray.add(block)
            if (block.previousHash.isEmpty()) { // prevHash가 없으면 genesis block.
                break
            }

            currentBlockHash = block.previousHash
        }

        return@runBlocking resultArray
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