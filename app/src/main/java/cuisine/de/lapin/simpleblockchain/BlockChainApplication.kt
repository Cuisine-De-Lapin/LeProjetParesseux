package cuisine.de.lapin.simpleblockchain

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class BlockChainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        plant(DebugTree())
    }
}