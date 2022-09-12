package cuisine.de.lapin.simpleblockchain.blockchain.model

import com.google.gson.Gson
import cuisine.de.lapin.simpleblockchain.utils.sha256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BlockChain(difficulty: Int) {
    private val blocks = ArrayList<Block>()
    private val startZeros = ZERO.repeat(difficulty)
    private var onReady: (() -> Unit)? = null

    suspend fun initChain(timeStamp: Long = System.currentTimeMillis()): Block {
        val block = createBlock(
            event = GenesisEvent(GENESIS_EVENT),
            timeStamp = timeStamp
        )

        onReady?.invoke()

        return block
    }

    suspend fun createBlock(
        event: Event,
        timeStamp: Long = System.currentTimeMillis()
    ): Block {
        val blockData = ProofData(
            index = (getPreviousBlock()?.index?.plus(1)) ?: 0,
            timeStamp = timeStamp,
            event = event,
            previousHash = getPreviousBlock()?.hash ?: ZERO,
            nonce = INIT_NONCE
        )

        while (true) {
            if (Gson().toJson(blockData).sha256().startsWith(startZeros)) {
                break
            } else {
                blockData.nonce++
            }
        }

        return blockData.toBlock(Gson().toJson(blockData).sha256()).apply {
            blocks.add(this)
        }
    }

    private fun getPreviousBlock(): Block? {
        return blocks.lastIndex.takeIf { it >= 0 }?.let {
            blocks[it]
        }
    }

    fun setOnReadyListener(onReady: (() -> Unit)?) {
        this.onReady = onReady
        if (blocks.size > 0) {
            onReady?.invoke()
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

    fun getAllBlocks(): List<Block> = blocks

    companion object {
        private const val ZERO = "0"
        private const val GENESIS_EVENT = "GenesisEvent"
        private const val INIT_NONCE = 0L

        fun createBlockChain(
            coroutineScope: CoroutineScope,
            difficulty: Int,
            timeStamp: Long = System.currentTimeMillis()
        ): BlockChain {
            return BlockChain(difficulty).apply {
                coroutineScope.launch {
                    initChain(timeStamp)
                }
            }
        }
    }

}