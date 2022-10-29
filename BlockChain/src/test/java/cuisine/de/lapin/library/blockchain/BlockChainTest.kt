package cuisine.de.lapin.library.blockchain

import android.os.Looper
import cuisine.de.lapin.library.blockchain.model.BlockChainImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.mockito.Mockito

class BlockChainTest {
    @Test
    fun is_BlockChain_Created() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChainImpl.createBlockChain(3)
        blockChain.addBlock("First Block", System.currentTimeMillis())
        blockChain.addBlock("Second Block", System.currentTimeMillis())
        blockChain.addBlock("Third Block", System.currentTimeMillis())
        blockChain.showBlocks()
        assert(blockChain.isValidChain())
    }

    @Test
    fun is_BlockChain_Created_With_Other_Coroutines() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChainImpl.createBlockChain(3)

        CoroutineScope(Dispatchers.Default).launch {
            println("Adding First Block")
            blockChain.addBlock("First Block", System.currentTimeMillis())
            println("Complete to Add First Block")
        }.join()

        CoroutineScope(Dispatchers.Default).launch {
            println("Adding Second Block")
            blockChain.addBlock("Second Block", System.currentTimeMillis())
            println("Complete to Add Second Block")
        }.join()

        CoroutineScope(Dispatchers.Default).launch {
            println("Adding Third Block")
            blockChain.addBlock("Third Block", System.currentTimeMillis())
            println("Complete to Add Third Block")
        }.join()

        CoroutineScope(Dispatchers.Default).launch {
            println("Adding Fourth Block")
            blockChain.addBlock("Fourth Block", System.currentTimeMillis())
            println("Complete to Add Fourth Block")
        }.join()

        blockChain.showBlocks()
        assert(blockChain.isValidChain())
    }
}