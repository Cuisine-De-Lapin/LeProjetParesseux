package cuisine.de.lapin.simpleblockchain.repository

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.model.Block
import javax.inject.Inject

class BlockRepository @Inject constructor(private val blockChain: BlockChain){
    suspend fun getCurrentBlockChain(): List<Block> {
        return blockChain.getBlockChainAsList()
    }

    suspend fun addBlock(content: Any, timeStamp: Long): List<Block> {
        blockChain.addBlock(content, timeStamp)
        return blockChain.getBlockChainAsList()
    }
}