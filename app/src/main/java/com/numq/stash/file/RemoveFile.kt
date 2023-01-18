package com.numq.stash.file

import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.interactor.UseCase

class RemoveFile constructor(
    private val repository: FileRepository
) : UseCase<File, CancellableAction>() {
    override suspend fun execute(arg: File) = repository.removeFile(arg).action()
}