package com.numq.stash.sharing

import com.numq.stash.files.ImageFile
import kotlinx.coroutines.flow.Flow

interface SharingApi {
    val files: Flow<ImageFile>
    fun startSharing(): Boolean
    fun stopSharing(): Boolean
    fun refresh(): Boolean
    fun shareFile(file: ImageFile): Boolean
}