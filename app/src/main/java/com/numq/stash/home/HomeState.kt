package com.numq.stash.home

data class HomeState(val imageFiles: List<ImageFile> = emptyList(), val exception: Exception? = null)