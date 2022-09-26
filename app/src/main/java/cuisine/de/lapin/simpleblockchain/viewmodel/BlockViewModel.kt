package cuisine.de.lapin.simpleblockchain.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.model.BabyEvent
import cuisine.de.lapin.simpleblockchain.model.BabyEventType
import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.temporal.TemporalAmount
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val repository: BlockRepository
) : ViewModel() {
    private val _blockChain = MutableLiveData<List<Block>>()
    val blockChain: LiveData<List<Block>> = _blockChain

    fun createBlock(selectedType: BabyEventType, timeStamp: Long, amount: Double, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createBlock(
                BabyEvent(selectedType, timeStamp, amount, comment),
                System.currentTimeMillis()
            )
            updateBlockChain()
        }
    }

    private fun updateBlockChain() {
        _blockChain.postValue(ArrayList(repository.getAllBlocks()))
    }

    fun initBlockChain() {
        repository.setOnReadyListener {
            updateBlockChain()
        }
    }

}