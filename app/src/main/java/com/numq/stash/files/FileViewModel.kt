package com.numq.stash.files

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FileViewModel constructor(
    getEvents: GetEvents,
    private val clearFiles: ClearFiles,
    private val startSharing: StartSharing,
    private val stopSharing: StopSharing,
    private val refresh: Refresh,
    private val shareFile: ShareFile,
    private val uploadFile: UploadFile,
    private val downloadOneFile: DownloadOneFile,
    private val downloadMultipleFiles: DownloadMultipleFiles,
    private val downloadZip: DownloadZip,
) : ViewModel(), LifecycleObserver {

    private val _state = MutableStateFlow(FilesState())
    val state: StateFlow<FilesState> = _state.asStateFlow()

    init {
        getEvents.invoke(Unit) {
            it.fold(onError) { events ->
                viewModelScope.launch {
                    events.collect { event ->
                        when (event) {
                            is FileEvent.Clear -> {
                                _state.update { s ->
                                    s.copy(imageFiles = emptyList())
                                }
                            }
                            is FileEvent.Refresh -> {
                                state.value.imageFiles.forEach { file ->
                                    shareFile.invoke(file) { result ->
                                        result.fold(onError) {}
                                    }
                                }
                            }
                            is FileEvent.File -> {
                                if (!state.value.imageFiles.contains(event.file)) {
                                    _state.update { s ->
                                        s.copy(imageFiles = s.imageFiles.plus(event.file))
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    fun clearFiles() = clearFiles.invoke(Unit) { it.fold(onError) {} }

    fun startSharing() = startSharing.invoke(Unit) {
        it.fold(onError) { connected ->
            if (connected) _state.update { s ->
                s.copy(isSharing = connected)
            }.also { refresh() }
        }
    }

    fun stopSharing() =
        stopSharing.invoke(Unit) {
            it.fold(onError) {
                _state.update { s ->
                    s.copy(
                        isSharing = it,
                        imageFiles = emptyList()
                    )
                }
            }
        }

    fun refresh() = refresh.invoke(Unit) { it.fold(onError) {} }

    fun uploadFile(uri: String) = uploadFile.invoke(uri) { it.fold(onError) {} }

    fun downloadOneFile(file: ImageFile) = downloadOneFile.invoke(file) { it.fold(onError) {} }

    fun downloadMultipleFiles(files: List<ImageFile>) =
        downloadMultipleFiles.invoke(files) { it.fold(onError) {} }

    fun downloadZip(files: List<ImageFile>) = downloadZip.invoke(files) { it.fold(onError) {} }
}