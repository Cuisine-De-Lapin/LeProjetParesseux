package cuisine.de.lapin.simpleblockchain.repository

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.library.blockchain.model.Block
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(private val blockChain: BlockChain): BlockRepository {
    override suspend fun getCurrentBlockChain(): List<Block> {
        return blockChain.getBlockChainAsList()
    }

    override suspend fun addBlock(content: Any, timeStamp: Long): List<Block> {
        blockChain.addBlock(content, timeStamp)
        return blockChain.getBlockChainAsList()
    }

    override fun getBlock(hash: String): Block? {
        return blockChain.getBlock(hash)
    }
}