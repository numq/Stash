package com.numq.stash.folder

import com.numq.stash.file.File

data class FolderState(
    val sharingStatus: SharingStatus = SharingStatus.Offline,
    val lastAvailableAddress: String? = null,
    val files: List<File> = emptyList(),
    val previewFile: File? = null,
    val filteredByExtension: Boolean = false,
    val selectedFiles: List<File> = emptyList(),
    val networkInfoVisible: Boolean = false,
    val configurationVisible: Boolean = false,
)