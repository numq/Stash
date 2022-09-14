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
    private val getEvents: GetEvents,
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

    private fun observeEvents() = getEvents.invoke(Unit, onError) { events ->
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
                            shareFile.invoke(file, onError)
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

    init {
        observeEvents()
    }

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    fun clearFiles() = clearFiles.invoke(Unit, onError)

    fun startSharing() = startSharing.invoke(Unit, onError) { connected ->
        if (connected) _state.update { s ->
            s.copy(isSharing = connected)
        }.also { refresh() }
    }

    fun stopSharing() = stopSharing.invoke(Unit, onError) {
        _state.update { s ->
            s.copy(
                isSharing = it,
                imageFiles = emptyList()
            )
        }
    }

    fun refresh() = refresh.invoke(Unit, onError)

    fun uploadFile(uri: String) = uploadFile.invoke(uri, onError)

    fun downloadOneFile(file: ImageFile) = downloadOneFile.invoke(file, onError)

    fun downloadMultipleFiles(files: List<ImageFile>) = downloadMultipleFiles.invoke(files, onError)

    fun downloadZip(files: List<ImageFile>) = downloadZip.invoke(files, onError)
}