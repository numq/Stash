package com.numq.stash.sharing

import com.numq.stash.files.FileEvent
import com.numq.stash.files.ImageFile
import kotlinx.coroutines.flow.Flow

interface SharingApi {
    val events: Flow<FileEvent>
    fun clear(): Boolean
    fun refresh(): Boolean
    fun startSharing(): Boolean
    fun stopSharing(): Boolean
    fun shareFile(file: ImageFile): Boolean
}