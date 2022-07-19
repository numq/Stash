package com.numq.stash.load

import com.numq.stash.files.ImageFile

interface LoadApi {
    fun upload(uri: String, onUpload: (ImageFile) -> Boolean = { false }): Boolean
    fun downloadOne(file: ImageFile): Boolean
    fun downloadMultiple(files: List<ImageFile>): Boolean
    fun downloadZip(files: List<ImageFile>): Boolean
}