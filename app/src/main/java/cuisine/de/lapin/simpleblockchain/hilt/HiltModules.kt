package cuisine.de.lapin.simpleblockchain.hilt

import cuisine.de.lapin.simpleblockchain.BuildConfig
import cuisine.de.lapin.library.blockchain.model.BlockChainImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlockChainModule {
    @Singleton
    @Provides
    fun provideBlockChain(): BlockChainImpl {
        return BlockChainImpl.createBlockChain(BuildConfig.BLOCKCHAIN_DIFFICULTY, System.currentTimeMillis())
    }
}