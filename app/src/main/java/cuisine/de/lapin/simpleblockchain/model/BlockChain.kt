package cuisine.de.lapin.simpleblockchain.model

import com.google.gson.Gson
import cuisine.de.lapin.simpleblockchain.utils.sha256

class BlockChain(difficulty: Int, timeStamp: Long = System.currentTimeMillis()) {
    private val blocks = ArrayList<Block>()
    private val startZeros = START_WITH.repeat(difficulty)

    init {
        createBlock(
            event = Event(GENESIS_EVENT),
            timeStamp = timeStamp
        )
    }

    fun createBlock(
        event: Event,
        timeStamp: Long = System.currentTimeMillis()
    ): Block {
        var nonce = 0L
        val blockData = ProofData(
            index = (getPreviousBlock()?.index?.plus(1)) ?: 0,
            timeStamp = timeStamp,
            event = event,
            previousHash = getPreviousBlock()?.hash ?: "0"
        )

        while (true) {
            blockData.nonce = nonce
            if (Gson().toJson(blockData).sha256().startsWith(startZeros)) {
                break
            } else {
                nonce++
            }
        }

        val hash = Gson().toJson(blockData).sha256()

        val block = blockData.toBlock(hash)

        blocks.add(block)

        println(Gson().toJson(block))

        return block
    }

    private fun getPreviousBlock(): Block? {
        return blocks.lastIndex.takeIf { it >= 0 }?.let {
            blocks[it]
        }
    }

    fun isValidChain(): Boolean {
        var previousBlock: Block? = null

        blocks.forEach { block ->
            if (isBlockValid(block).not()) {
                return false
            }

            previousBlock?.let { previousBlock ->
                if (previousBlock.hash != block.previousHash) {
                    return false
                }
            }

            previousBlock = block
        }

        return true
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.run {
            hash == Gson().toJson(toProofData()).sha256()
        }
    }

    fun getAllBlocks(): List<Block> {
        println(Gson().toJson(blocks))
        return blocks
    }

    companion object {
        private const val START_WITH = "0"
        private const val GENESIS_EVENT = "GenesisEvent"
    }

}