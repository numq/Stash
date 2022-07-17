package com.numq.stash.sharing

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel constructor(
    getFiles: GetFiles,
    private val startSharing: StartSharing,
    private val stopSharing: StopSharing,
    private val refresh: Refresh,
    private val sendFile: SendFile
) : ViewModel(), LifecycleObserver {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        getFiles.invoke(Unit) {
            it.fold(onError) { files ->
                viewModelScope.launch {
                    files.collect { file ->
                        if (!state.value.imageFiles.contains(file)) {
                            _state.update { s ->
                                s.copy(imageFiles = s.imageFiles.plus(file))
                            }
                        }
                    }
                }
            }
        }
    }

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    fun startSharing() =
        startSharing.invoke(Unit) {
            it.fold(onError) { connected ->
                if (connected) _state.update { s ->
                    s.copy(
                        isSharing = connected
                    )
                }.also { refresh() }
            }
        }

    fun stopSharing() =
        stopSharing.invoke(Unit) { it.fold(onError) { _state.update { s -> s.copy(isSharing = it) } } }

    fun refresh() = refresh.invoke(Unit) { it.fold(onError) {} }

    fun sendFile(imageFile: ImageFile) = sendFile.invoke(imageFile) { it.fold(onError) {} }
}