package cuisine.de.lapin.library.blockchain

import cuisine.de.lapin.library.blockchain.model.BlockChain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockChainTest {

    @Test
    fun is_BlockChain_Created() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChain.createBlockChain(3, this)
        blockChain.addBlock("First Block")
        blockChain.addBlock("Second Block")
        blockChain.addBlock("Third Block")
        blockChain.showBlocks()
        assert(blockChain.isValidChain())
    }
}