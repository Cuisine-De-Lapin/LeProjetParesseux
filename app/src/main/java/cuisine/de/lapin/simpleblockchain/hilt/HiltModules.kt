package cuisine.de.lapin.simpleblockchain.hilt

import android.content.Context
import cuisine.de.lapin.library.blockchain.interfaces.BlockChain
import cuisine.de.lapin.simpleblockchain.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlockChainModule {
    @Singleton
    @Provides
    fun provideBlockChain(@ApplicationContext context: Context): BlockChain {
        return BlockChain.createBlockChain(context.filesDir.path, BuildConfig.BLOCKCHAIN_DIFFICULTY.toUInt(), System.currentTimeMillis(), false)
    }
}