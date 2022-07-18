package com.numq.stash.files

data class FilesState(
    val isSharing: Boolean = false,
    val imageFiles: List<ImageFile> = emptyList(),
    val exception: Exception? = null
)