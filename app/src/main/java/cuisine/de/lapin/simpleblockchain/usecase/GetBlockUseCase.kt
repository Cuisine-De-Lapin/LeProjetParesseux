package cuisine.de.lapin.simpleblockchain.usecase

import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import javax.inject.Inject

class GetBlockUseCase @Inject constructor(private val blockRepository: BlockRepository) {
    operator fun invoke(hash: String): Block? {
        return blockRepository.getBlock(hash)
    }
}