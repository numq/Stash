package com.numq.stash.file

import arrow.core.flatMap
import arrow.core.right
import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.interactor.UseCase

class ShareFile constructor(
    private val repository: FileRepository
) : UseCase<Triple<String, String, ByteArray>, CancellableAction>() {
    override suspend fun execute(arg: Triple<String, String, ByteArray>) =
        arg.right().flatMap { (name, extension, bytes) ->
            repository.shareFile(name, extension, bytes).action()
        }
}