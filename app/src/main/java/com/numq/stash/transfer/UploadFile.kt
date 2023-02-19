package com.numq.stash.transfer

import arrow.core.flatMap
import arrow.core.right
import com.numq.stash.file.FileRepository
import com.numq.stash.interactor.UseCase

class UploadFile constructor(
    private val repository: FileRepository
) : UseCase<Triple<String, String, ByteArray>, Unit>() {
    override suspend fun execute(arg: Triple<String, String, ByteArray>) =
        arg.right().flatMap { (name, extension, bytes) ->
            repository.shareFile(name, extension, bytes)
        }
}