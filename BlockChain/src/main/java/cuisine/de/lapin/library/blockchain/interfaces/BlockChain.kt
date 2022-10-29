package cuisine.de.lapin.library.blockchain.interfaces

import cuisine.de.lapin.library.blockchain.model.Block

interface BlockChain {
    suspend fun addBlock(content: Any, timeStamp: Long)
    fun changeDifficulty(difficulty: Int)
    suspend fun isValidChain(): Boolean
    suspend fun getBlockChainAsList(): List<Block>
}