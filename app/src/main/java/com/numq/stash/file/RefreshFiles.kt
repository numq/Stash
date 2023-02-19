package com.numq.stash.file

import com.numq.stash.interactor.UseCase

class RefreshFiles constructor(
    private val repository: FileRepository
) : UseCase<Unit, Unit>() {
    override suspend fun execute(arg: Unit) = repository.refreshFiles()
}