package com.example.kotlinflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MyViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow<Int>(-1)
    private val _sharedFlow = MutableSharedFlow<Int>()

    private val _liveData = MutableLiveData<Int>()

    val stateFlow = _stateFlow.asStateFlow()
    val sharedFlow = _sharedFlow.asSharedFlow()
    val liveData: LiveData<Int> = _liveData

    val hybridStateFlow =
        _sharedFlow.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, -1)
//        _sharedFlow.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, -1)
//        _sharedFlow.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.WhileSubscribed(5000), -1)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0..20) {
                delay(500)
                _sharedFlow.emit(i)
                _stateFlow.emit(i)
//                _liveData.value = i
                _liveData.postValue(i)
            }
        }
    }
}
