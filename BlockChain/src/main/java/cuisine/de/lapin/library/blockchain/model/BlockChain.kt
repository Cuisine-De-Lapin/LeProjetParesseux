package cuisine.de.lapin.library.blockchain.model

import android.os.Looper
import cuisine.de.lapin.library.blockchain.utils.assertNotMainThread
import cuisine.de.lapin.library.blockchain.utils.sha256
import cuisine.de.lapin.library.blockchain.utils.toBlock
import cuisine.de.lapin.library.blockchain.utils.toJson
import cuisine.de.lapin.library.blockchain.utils.toPayLoad

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
        assertNotMainThread("access blockchain")

        val block =
            Block.createBlock(content, _lastestBlockHash, ++_height, timeStamp, difficulty)
        _height = block.height
        _lastestBlockHash = block.hash
        _blocks[block.hash] = block.toJson()
        onUpdateChain?.invoke(_blocks)
    }

    fun changeDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }

    fun isValidChain(): Boolean {
        assertNotMainThread("access blockchain")
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
        assertNotMainThread("access blockchain")
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