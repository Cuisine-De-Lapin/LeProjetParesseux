package cuisine.de.lapin.library.blockchain

import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockChainTest {
    @Test
    fun is_BlockChain_Created() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChain.createBlockChain(defaultDifficulty = 3u, dataBasePath = "./src/androidTest")
        blockChain.addBlock("First Block", System.currentTimeMillis())
        blockChain.addBlock("Second Block", System.currentTimeMillis())
        blockChain.addBlock("Third Block", System.currentTimeMillis())
        println(blockChain.getBlockChainAsList())
        assert(blockChain.isValidChain())
    }

    @Test
    fun is_BlockChain_Created_With_Other_Coroutines() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChain.createBlockChain(defaultDifficulty = 3u, dataBasePath = "./src/androidTest")

        CoroutineScope(Dispatchers.IO).launch {
            println("Adding First Block")
            blockChain.addBlock("First Block", System.currentTimeMillis())
            println("Complete to Add First Block")
        }.join()

        CoroutineScope(Dispatchers.Default).launch {
            println("Adding Second Block")
            blockChain.addBlock("Second Block", System.currentTimeMillis())
            println("Complete to Add Second Block")
        }.join()

        launch {
            println("Adding Third Block")
            blockChain.addBlock("Third Block", System.currentTimeMillis())
            println("Complete to Add Third Block")
        }.join()

        CoroutineScope(Dispatchers.Unconfined).launch {
            println("Adding Fourth Block")
            blockChain.addBlock("Fourth Block", System.currentTimeMillis())
            println("Complete to Add Fourth Block")
        }.join()

        println(blockChain.getBlockChainAsList())
        assert(blockChain.isValidChain())
    }
}