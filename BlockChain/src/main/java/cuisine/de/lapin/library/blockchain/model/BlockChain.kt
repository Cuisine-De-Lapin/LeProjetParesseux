package cuisine.de.lapin.library.blockchain.model

import cuisine.de.lapin.simpleblockchain.utils.sha256
import cuisine.de.lapin.simpleblockchain.utils.toBlock
import cuisine.de.lapin.simpleblockchain.utils.toJson
import cuisine.de.lapin.simpleblockchain.utils.toPayLoad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlockChain(
    private var difficulty: Int,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val blocks = HashMap<String, String>()
    private var lastestBlockHash: String = ""
    private var height: UInt = 0u

    fun addBlock(content: Any, timeStamp: Long = System.currentTimeMillis()) {
        coroutineScope.launch {
            val block =
                Block.createBlock(content, lastestBlockHash, ++height, timeStamp, difficulty)
            height = block.height
            lastestBlockHash = block.hash
            blocks[block.hash] = block.toJson()
        }
    }

    fun changeDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }

    fun isValidChain(): Boolean {
        var currentBlockHash: String? = lastestBlockHash
        while (true) {
            val block = blocks[currentBlockHash]?.toBlock() ?: break
            if (block.content == GENESIS_EVENT) {
                return true
            }

            currentBlockHash = block.previousHash
        }

        return false
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == block.toPayLoad().sha256()
        }
    }

    fun showBlocks() {
        for (block in blocks) {
            println(block.value)
        }
    }

    companion object {
        private const val GENESIS_EVENT = "GenesisEvent"

        fun createBlockChain(
            difficulty: Int,
            coroutineScope: CoroutineScope,
            timeStamp: Long = System.currentTimeMillis()
        ): BlockChain {
            return BlockChain(difficulty, coroutineScope).apply {
                addBlock(GENESIS_EVENT, timeStamp)
            }
        }

    }

}