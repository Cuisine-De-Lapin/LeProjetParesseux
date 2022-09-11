package cuisine.de.lapin.simpleblockchain

import cuisine.de.lapin.simpleblockchain.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.blockchain.model.BlockChain
import cuisine.de.lapin.simpleblockchain.blockchain.model.Event
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockChainTest {
    private suspend fun createBlockChain(): BlockChain {
        val timeStamp = 0L
        val difficulty = 1
        val blockChain = BlockChain(difficulty)
        blockChain.initChain(timeStamp)
        return blockChain

    }

    @Test
    fun is_BlockChain_Created() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = createBlockChain()
        assert(blockChain.isValidChain())
    }

    @Test
    fun is_BlockChain_FirstItem() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = createBlockChain()
        val timeStamp = 0L
        val anticipatedHash = "0657f235d2eee7d7f1c914044ff16c22904e0232052cac41f08b4d76ccf4e28a"
        val createdBlock = blockChain.createBlock(event = Event("First Block"), timeStamp = timeStamp)

        assert(blockChain.isValidChain() && anticipatedHash == createdBlock.hash)
    }

    @Test
    fun is_BlockChain_Added() = runTest(UnconfinedTestDispatcher()) {
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

    @Test
    fun createGenesisBlockAsync() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChain.createBlockChain(this, 1, 0L)
        blockChain.setOnReadyListener {
            assert(blockChain.isValidChain())
        }
    }
}