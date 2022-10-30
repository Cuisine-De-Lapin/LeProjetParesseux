package cuisine.de.lapin.simpleblockchain.usecase

import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import javax.inject.Inject

class AddBlockUseCase @Inject constructor(private val blockRepository: BlockRepository) {
    suspend operator fun invoke(contentAny: Any, timeStamp: Long): List<Block> {
        return blockRepository.addBlock(contentAny, timeStamp)
    }
}