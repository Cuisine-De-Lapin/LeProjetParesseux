package cuisine.de.lapin.simpleblockchain.repository

import cuisine.de.lapin.library.blockchain.model.Block

interface BlockRepository {
    suspend fun getCurrentBlockChain(): List<Block>
    suspend fun addBlock(content: Any, timeStamp: Long): List<Block>
    fun getBlock(hash: String): Block?
}