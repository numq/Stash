package com.numq.stash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class StateViewModel<T>(initialValue: T) : ViewModel() {

    private val _state: MutableStateFlow<T> = MutableStateFlow(initialValue)
    val state: StateFlow<T> = _state.asStateFlow()

    private val _exception: Channel<Exception> = Channel()
    val exception: Flow<Exception> = _exception.receiveAsFlow()

    val onException: (Exception) -> Unit = { viewModelScope.launch { _exception.send(it) } }

    fun updateState(f: (T) -> T) = _state.update(f)

}