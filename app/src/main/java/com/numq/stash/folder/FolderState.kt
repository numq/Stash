package com.numq.stash.folder

import com.numq.stash.file.File

data class FolderState(
    val sharingStatus: SharingStatus = SharingStatus.OFFLINE,
    val files: List<File> = emptyList(),
    val previewFile: File? = null,
    val filteredByExtension: Boolean = false,
    val selectedFiles: List<File> = emptyList()
)