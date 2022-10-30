package cuisine.de.lapin.simpleblockchain.usecase

import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import timber.log.Timber
import javax.inject.Inject

class GetBlockUseCase @Inject constructor(private val blockRepository: BlockRepository) {
    suspend operator fun invoke(): List<Block> {
        return blockRepository.getCurrentBlockChain()
    }
}