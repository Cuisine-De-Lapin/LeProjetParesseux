package cuisine.de.lapin.simpleblockchain.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.model.BabyEvent
import cuisine.de.lapin.simpleblockchain.model.BabyEventType
import cuisine.de.lapin.simpleblockchain.usecase.AddBlockUseCase
import cuisine.de.lapin.simpleblockchain.usecase.GetBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val addBlockUseCase: AddBlockUseCase,
    private val getBlockUseCase: GetBlockUseCase
) : ViewModel() {
    private val _blockChain = MutableLiveData<List<Block>>()
    val blockChain: LiveData<List<Block>> = _blockChain

    fun addBlock(selectedType: BabyEventType, timeStamp: Long, amount: Double, comment: String) {
        viewModelScope.launch {
            _blockChain.postValue(addBlockUseCase.invoke(BabyEvent(selectedType, timeStamp, amount, comment), System.currentTimeMillis()))
        }
    }

    fun loadBlocks() {
        viewModelScope.launch {
            _blockChain.postValue(getBlockUseCase.invoke())
        }
    }

}