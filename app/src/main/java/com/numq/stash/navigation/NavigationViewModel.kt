package com.numq.stash.navigation

import androidx.lifecycle.viewModelScope
import com.numq.stash.file.File
import com.numq.stash.transfer.DownloadFile
import com.numq.stash.transfer.DownloadZip
import com.numq.stash.transfer.GetTransferActions
import com.numq.stash.transfer.UploadFile
import com.numq.stash.viewmodel.StateViewModel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class NavigationViewModel constructor(
    private val getTransferActions: GetTransferActions,
    private val uploadFile: UploadFile,
    private val downloadFile: DownloadFile,
    private val downloadZip: DownloadZip
) : StateViewModel<NavigationState>(NavigationState()) {

    private fun observeTransferEvents() {
        getTransferActions.invoke(viewModelScope, Unit, onException) { channel ->
            viewModelScope.launch {
                channel.consumeAsFlow().collect { action ->
                    updateState { it.copy(action = action) }
                }
            }
        }
    }

    init {
        observeTransferEvents()
    }

    fun uploadFiles(files: List<Triple<String, String, ByteArray>>) =
        viewModelScope.launch {
            files.forEach { (name, extension, bytes) ->
                uploadFile.invoke(viewModelScope, Triple(name, extension, bytes), onException)
            }
        }

    fun downloadFile(uri: String, file: File) =
        downloadFile.invoke(viewModelScope, Pair(uri, file), onException)

    fun downloadZip(uri: String, files: List<File>) =
        downloadZip.invoke(viewModelScope, Pair(uri, files), onException)

    fun completeAction() = updateState { it.copy(action = null) }

}