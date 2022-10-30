package cuisine.de.lapin.simpleblockchain.hilt

import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import cuisine.de.lapin.simpleblockchain.repository.BlockRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModules {
    @Binds
    abstract fun bindBlockRepository(impl: BlockRepositoryImpl): BlockRepository
}