package com.numq.stash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class StateViewModel<T> constructor(initialValue: T) : ViewModel() {

    private val _state: MutableStateFlow<T> by lazy { MutableStateFlow(initialValue) }
    val state: StateFlow<T> = _state.asStateFlow()

    private val _exception: Channel<Exception?> = Channel()
    val exception: Channel<Exception?> = _exception

    val onException: (Exception?) -> Unit = { viewModelScope.launch { _exception.send(it) } }

    fun updateState(f: (T) -> T) = _state.update(f)

}