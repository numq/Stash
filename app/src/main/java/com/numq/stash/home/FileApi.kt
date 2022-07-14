package com.numq.stash.home

import kotlinx.coroutines.channels.Channel

interface FileApi {
    val files: Channel<ImageFile>
    fun refresh()
    fun sendFile(imageFile: ImageFile)
}