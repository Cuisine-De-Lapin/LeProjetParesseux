package cuisine.de.lapin.library.blockchain

import cuisine.de.lapin.library.blockchain.model.BlockChain
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
        val anticipatedHash = "0519be7410980961d2fd8dfab92f5433dc2448424e8d6b2eddab20b8a25ebf11"
        val createdBlock = blockChain.createBlock(content = TestEvent("First Block"), timeStamp = timeStamp)

        assert(blockChain.isValidChain() && anticipatedHash == createdBlock.hash)
    }

    @Test
    fun is_BlockChain_Added() = runTest(UnconfinedTestDispatcher()) {
        val timeStamp = 91152000L // 1972.11.21. 00:00:00 GMT+0000
        val blockChain = createBlockChain()

        val anticipatedHash1 = "0519be7410980961d2fd8dfab92f5433dc2448424e8d6b2eddab20b8a25ebf11"
        val createdBlock1 = blockChain.createBlock(TestEvent("First Block"), timeStamp = timeStamp)

        assert(anticipatedHash1 == createdBlock1.hash)

        val anticipatedHash2 = "074ecc218cd216782e24f38c77ede975e052ea50fe8e0461246e6cc9d7bd8b9c"
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

    inner class TestEvent(message: String)
}