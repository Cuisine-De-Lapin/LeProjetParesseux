package cuisine.de.lapin.library.blockchain.model

import android.os.Looper
import cuisine.de.lapin.simpleblockchain.utils.sha256
import cuisine.de.lapin.simpleblockchain.utils.toBlock
import cuisine.de.lapin.simpleblockchain.utils.toJson
import cuisine.de.lapin.simpleblockchain.utils.toPayLoad

class BlockChain(
    private var difficulty: Int
) {
    private val _blocks = HashMap<String, String>()
    private var _lastestBlockHash: String = ""
    private var _height: UInt = 0u

    val blocks: Map<String, String> = _blocks
    val lastestBlockHash
        get() = _lastestBlockHash
    val height
        get() = _height

    var onUpdateChain: ((Map<String, String>)->Unit)? = null

    fun addBlock(content: Any, timeStamp: Long = System.currentTimeMillis()) {
        assertNotMainThread()

        val block =
            Block.createBlock(content, _lastestBlockHash, ++_height, timeStamp, difficulty)
        _height = block.height
        _lastestBlockHash = block.hash
        _blocks[block.hash] = block.toJson()
        onUpdateChain?.invoke(_blocks)
    }

    private fun assertNotMainThread() {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            throw java.lang.IllegalStateException(
                "Cannot access blockchain on the main thread since"
                        + " it may potentially lock the UI for a long period of time."
            )
        }
    }


    fun changeDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }

    fun isValidChain(): Boolean {
        var currentBlockHash: String? = _lastestBlockHash
        while (true) {
            val block = _blocks[currentBlockHash]?.toBlock() ?: break
            if (block.content == GENESIS_EVENT) {
                return true
            }

            currentBlockHash = block.previousHash
        }

        return false
    }

    fun toList(): List<Block> {
        val resultArray = ArrayList<Block>()

        var currentBlockHash: String? = _lastestBlockHash
        while (true) {
            val block = _blocks[currentBlockHash]?.toBlock() ?: return emptyList()
            if (block.content == GENESIS_EVENT) {
                break
            }

            currentBlockHash = block.previousHash
        }

        return resultArray
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == block.toPayLoad().sha256()
        }
    }

    fun showBlocks() {
        for (block in _blocks) {
            println(block.value)
        }
    }

    companion object {
        private const val GENESIS_EVENT = "GenesisEvent"

        fun createBlockChain(
            difficulty: Int,
            timeStamp: Long = System.currentTimeMillis()
        ): BlockChain {
            return BlockChain(difficulty).apply {
                addBlock(GENESIS_EVENT, timeStamp)
            }
        }
    }

}