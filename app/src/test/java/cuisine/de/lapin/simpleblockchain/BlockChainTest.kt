package cuisine.de.lapin.simpleblockchain

import cuisine.de.lapin.simpleblockchain.model.BlockChain
import cuisine.de.lapin.simpleblockchain.model.Event
import org.junit.Test

class BlockChainTest {
    private fun createBlockChain(): BlockChain {
        val timeStamp = 0L
        val difficulty = 1
        return BlockChain(difficulty = difficulty, timeStamp = timeStamp)
    }
    @Test
    fun is_BlockChain_Created() {
        val blockChain = createBlockChain()
        assert(blockChain.isValidChain())
    }

    @Test
    fun is_BlockChain_FirstItem() {
        val blockChain = createBlockChain()
        val timeStamp = 0L
        val anticipatedHash = "0657f235d2eee7d7f1c914044ff16c22904e0232052cac41f08b4d76ccf4e28a"
        val createdBlock = blockChain.createBlock(event = Event("First Block"), timeStamp = timeStamp)

        assert(blockChain.isValidChain() && anticipatedHash == createdBlock.hash)
    }

    @Test
    fun is_BlockChain_Added() {
        val timeStamp = 0L
        val blockChain = createBlockChain()

        val anticipatedHash1 = "0657f235d2eee7d7f1c914044ff16c22904e0232052cac41f08b4d76ccf4e28a"
        val createdBlock1 = blockChain.createBlock(Event("First Block"), timeStamp = timeStamp)

        assert(anticipatedHash1 == createdBlock1.hash)

        val anticipatedHash2 = "03f481a260bac7e8ee334086d839979e3a720a65ec6125392cf1029775a1eaad"
        val createdBlock2 = blockChain.createBlock(Event("Second Block"), timeStamp = timeStamp)

        assert(anticipatedHash2 == createdBlock2.hash)

        assert(blockChain.isValidChain())
    }
}