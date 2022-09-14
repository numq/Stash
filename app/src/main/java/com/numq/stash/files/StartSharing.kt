package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class StartSharing constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override suspend fun execute(arg: Unit) = repository.startSharing()
}