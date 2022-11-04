package cuisine.de.lapin.library.blockchain.interfaces

import cuisine.de.lapin.library.blockchain.impl.BlockChainImpl
import cuisine.de.lapin.library.blockchain.model.Block

interface BlockChain {
    suspend fun addBlock(content: Any, timeStamp: Long)
    suspend fun isValidChain(): Boolean
    suspend fun getBlockChainAsList(): List<Block>
    fun getBlock(hash: String): Block?
    fun close()

    companion object {
        fun createBlockChain(
            dataBasePath: String,
            defaultDifficulty: UInt = BlockChainImpl.DEFAULT_DIFFICULTY,
            timeStamp: Long = System.currentTimeMillis(),
            useDifficulty: Boolean = true
        ): BlockChain {
            return BlockChainImpl(dataBasePath, defaultDifficulty, timeStamp, useDifficulty)
        }
    }
}