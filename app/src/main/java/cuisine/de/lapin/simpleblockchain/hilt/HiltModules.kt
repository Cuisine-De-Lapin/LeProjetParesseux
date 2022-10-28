package cuisine.de.lapin.simpleblockchain.hilt

import cuisine.de.lapin.simpleblockchain.BuildConfig
import cuisine.de.lapin.library.blockchain.model.BlockChain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlockChainModule {
    @Singleton
    @Provides
    fun provideBlockChain(): BlockChain {
        return BlockChain.createBlockChain(BuildConfig.BLOCKCHAIN_DIFFICULTY, System.currentTimeMillis())
    }
}