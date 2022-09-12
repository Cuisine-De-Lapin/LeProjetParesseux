package cuisine.de.lapin.simpleblockchain

import cuisine.de.lapin.simpleblockchain.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.blockchain.model.BlockChain
import cuisine.de.lapin.simpleblockchain.blockchain.model.Event
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockChainTest {
    private suspend fun createBlockChain(): BlockChain {
        val timeStamp = 91152000L // 1972.11.21. 00:00:00 GMT+0000
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
        val timeStamp = 91152000L // 1972.11.21. 00:00:00 GMT+0000
        val anticipatedHash = "0699d0168c636520f53d277c59912b731dd800ca54363d168e76db9090cd8c36"
        val createdBlock = blockChain.createBlock(event = TestEvent("First Block"), timeStamp = timeStamp)

        assert(blockChain.isValidChain() && anticipatedHash == createdBlock.hash)
    }

    @Test
    fun is_BlockChain_Added() = runTest(UnconfinedTestDispatcher()) {
        val timeStamp = 91152000L // 1972.11.21. 00:00:00 GMT+0000
        val blockChain = createBlockChain()

        val anticipatedHash1 = "0699d0168c636520f53d277c59912b731dd800ca54363d168e76db9090cd8c36"
        val createdBlock1 = blockChain.createBlock(TestEvent("First Block"), timeStamp = timeStamp)

        assert(anticipatedHash1 == createdBlock1.hash)

        val anticipatedHash2 = "0c0392701f062f833c7051bbe7897dc2936bbf0e6a25946b971b1d7b1e829668"
        val createdBlock2 = blockChain.createBlock(TestEvent("Second Block"), timeStamp = timeStamp)

        assert(anticipatedHash2 == createdBlock2.hash)

        assert(blockChain.isValidChain())
    }

    @Test
    fun createGenesisBlockAsync() = runTest(UnconfinedTestDispatcher()) {
        val timeStamp = 91152000L // 1972.11.21. 00:00:00 GMT+0000
        val difficulty = 1
        val blockChain = BlockChain.createBlockChain(this, difficulty, timeStamp)
        blockChain.setOnReadyListener {
            assert(blockChain.isValidChain())
        }
    }

    inner class TestEvent(message: String): Event(message)
}