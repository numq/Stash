package com.numq.stash.files

import kotlinx.coroutines.flow.Flow

interface FileApi {
    val files: Flow<ImageFile>
    fun startSharing(): Boolean
    fun stopSharing(): Boolean
    fun refresh(): Boolean
    fun sendFile(file: ImageFile): Boolean
}