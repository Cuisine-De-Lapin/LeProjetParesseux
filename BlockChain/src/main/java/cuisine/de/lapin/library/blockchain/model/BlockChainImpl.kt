package cuisine.de.lapin.library.blockchain.model

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.utils.sha256
import cuisine.de.lapin.library.blockchain.utils.toBlock
import cuisine.de.lapin.library.blockchain.utils.toJson
import cuisine.de.lapin.library.blockchain.utils.toPayLoad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class BlockChainImpl(
    private var difficulty: Int,
) : BlockChain {
    private val _blocks = HashMap<String, String>()
    private var _lastestBlockHash: String = ""
    private var _height: UInt = 0u
    private val coroutineContext = newSingleThreadContext(THREAD_NAME) // 블록체인 전용 스레드

    val blocks: Map<String, String> = _blocks
    val lastestBlockHash
        get() = _lastestBlockHash
    val height
        get() = _height

    var onUpdateChain: ((Map<String, String>) -> Unit)? = null

    override suspend fun addBlock(content: Any, timeStamp: Long) {
        withContext(coroutineContext) {
            println("$content ${Thread.currentThread().name}")
            Block.createBlock(content, _lastestBlockHash, ++_height, timeStamp, difficulty)
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

        var currentBlockHash: String? = _lastestBlockHash
        while (true) {
            val block = _blocks[currentBlockHash]?.toBlock() ?: return@withContext emptyList()
            if (block.content == GENESIS_EVENT) {
                break
            }

            currentBlockHash = block.previousHash
        }

        return@withContext resultArray
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == block.toPayLoad().sha256()
        }
    }

    suspend fun showBlocks() {
        withContext(coroutineContext) {
            for (block in _blocks) {
                println(block.value)
            }
        }
    }

    companion object {
        private const val GENESIS_EVENT = "GenesisEvent"
        private const val THREAD_NAME = "BlockChainThread"

        fun createBlockChain(
            difficulty: Int,
            timeStamp: Long = System.currentTimeMillis()
        ): BlockChainImpl {
            return BlockChainImpl(difficulty).apply {
                CoroutineScope(coroutineContext).launch {
                    addBlock(GENESIS_EVENT, timeStamp)
                }
            }
        }
    }

}