package cuisine.de.lapin.library.blockchain.interfaces

import cuisine.de.lapin.library.blockchain.impl.BlockChainImpl
import cuisine.de.lapin.library.blockchain.model.Block

interface BlockChain {
    suspend fun addBlock(content: Any, timeStamp: Long)
    fun changeDifficulty(difficulty: Int)
    suspend fun isValidChain(): Boolean
    suspend fun getBlockChainAsList(): List<Block>
    fun setOnUpdateChainListener(onUpdateChain: (Map<String, String>)->Unit)
    fun getBlock(hash: String): Block?

    companion object {
        fun createBlockChain(
            difficulty: Int,
            timeStamp: Long = System.currentTimeMillis(),
            threadName: String? = null
        ): BlockChain {
            return BlockChainImpl(difficulty, timeStamp, threadName)
        }
    }
}