package cuisine.de.lapin.simpleblockchain.repository

import cuisine.de.lapin.simpleblockchain.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.blockchain.model.BlockChain
import cuisine.de.lapin.simpleblockchain.blockchain.model.Event
import javax.inject.Inject

class BlockRepository @Inject constructor(private val blockChain: BlockChain){
    fun setOnReadyListener(isReady: () -> Unit) {
        blockChain.setOnReadyListener(isReady)
    }

    suspend fun createBlock(event: Event, timeStamp: Long): Block {
        return blockChain.createBlock(
            event = event,
            timeStamp = timeStamp
        )
    }

    fun getAllBlocks() = blockChain.getAllBlocks()

}