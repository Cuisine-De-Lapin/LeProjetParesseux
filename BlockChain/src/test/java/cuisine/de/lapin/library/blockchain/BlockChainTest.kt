package cuisine.de.lapin.library.blockchain

import cuisine.de.lapin.library.blockchain.model.BlockChain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockChainTest {
    @Test
    fun is_BlockChain_Created() = runTest(UnconfinedTestDispatcher()) {
        val blockChain = BlockChain.createBlockChain(3)
        blockChain.addBlock("First Block")
        blockChain.addBlock("Second Block")
        blockChain.addBlock("Third Block")
        blockChain.showBlocks()
        assert(blockChain.isValidChain())
    }

    @Test
    fun is_BlockChain_Created_With_Other_Coroutines() {
//        val blockChain = runBlocking(UnconfinedTestDispatcher()) {
//            BlockChain.createBlockChain(3)
//        }
//
//        runBlocking {
//            blockChain.addBlock("First Block")
//        }
//
//        runBlocking {
//            blockChain.addBlock("Second Block")
//        }
//
//        runBlocking {
//            blockChain.addBlock("Third Block")
//        }
//
//        runBlocking {
//            blockChain.showBlocks()
//            assert(blockChain.isValidChain())
//        }

        val time1 = System.currentTimeMillis()
        val blockChain = BlockChain.createBlockChain(5)
        val time2 = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Default).launch {
            println("1 ${Thread.currentThread()}")
        }

        println("Time spent : ${time2-time1} ${Thread.currentThread()}")
        val time3 = delayedTime {
            blockChain.addBlock("First Block")
        }
        println("Time spent : ${time3} ${Thread.currentThread()}")
        val time4 = delayedTime {
            blockChain.addBlock("Second Block")
        }
        println("Time spent : ${time4} ${Thread.currentThread()}")
        blockChain.showBlocks()
    }

    private fun delayedTime(function: () -> Unit): Long {
        val time1 = System.currentTimeMillis()
        function.invoke()
        val time2 = System.currentTimeMillis()
        return time2 - time1

    }
}