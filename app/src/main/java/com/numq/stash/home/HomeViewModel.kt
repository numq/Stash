package com.numq.stash.home

import androidx.lifecycle.*
import com.numq.stash.websocket.SocketApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel constructor(
    private val socket: SocketApi,
    private val getFiles: GetFiles,
    private val refresh: Refresh,
    private val sendFile: SendFile
) : ViewModel(), LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (source.lifecycle.currentState) {
            Lifecycle.State.CREATED -> socket.connect()
            Lifecycle.State.DESTROYED -> socket.disconnect()
            else -> Unit
        }
    }

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    fun refresh() = refresh.invoke(Unit) { it.fold(onError) {} }

    fun sendFile(imageFile: ImageFile) = sendFile.invoke(imageFile) { it.fold(onError) {} }

    init {
        getFiles.invoke(Unit) {
            it.fold(onError) { files ->
                viewModelScope.launch {
                    files.consumeAsFlow().collectLatest { file ->
                        _state.update { s ->
                            s.copy(
                                imageFiles = s.imageFiles.plus(file).distinct()
                            )
                        }
                    }
                }
            }
        }
    }
}