package com.numq.stash.sharing

data class HomeState(
    val isSharing: Boolean = false,
    val imageFiles: List<ImageFile> = emptyList(),
    val exception: Exception? = null
)