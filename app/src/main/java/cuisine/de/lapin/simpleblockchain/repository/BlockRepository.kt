package cuisine.de.lapin.simpleblockchain.repository

import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.library.blockchain.model.BlockChain
import javax.inject.Inject

class BlockRepository @Inject constructor(private val blockChain: BlockChain){
    fun setOnReadyListener(isReady: () -> Unit) {
        blockChain.setOnReadyListener(isReady)
    }

    suspend fun createBlock(content: Any, timeStamp: Long): Block {
        return blockChain.createBlock(
            content = content,
            timeStamp = timeStamp
        )
    }

    fun getAllBlocks() = blockChain.getAllBlocks()

}