package com.numq.stash.sharing

import com.numq.stash.sharing.ImageFile

data class HomeState(
    val isSharing: Boolean = false,
    val imageFiles: List<ImageFile> = emptyList(),
    val exception: Exception? = null
)