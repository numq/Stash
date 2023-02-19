package com.numq.stash.file

import com.numq.stash.interactor.UseCase

class RemoveFile constructor(
    private val repository: FileRepository
) : UseCase<File, Unit>() {
    override suspend fun execute(arg: File) = repository.removeFile(arg)
}