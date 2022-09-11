package cuisine.de.lapin.simpleblockchain.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cuisine.de.lapin.simpleblockchain.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.blockchain.model.Event
import cuisine.de.lapin.simpleblockchain.repository.BlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val repository: BlockRepository
): ViewModel() {
    private val _blockChain = MutableLiveData<List<Block>>()
    val blockChain: LiveData<List<Block>> = _blockChain

    fun createBlock(eventContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createBlock(Event(eventContent), System.currentTimeMillis())
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