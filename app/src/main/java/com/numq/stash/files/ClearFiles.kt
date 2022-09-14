package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class ClearFiles constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override suspend fun execute(arg: Unit) = repository.clear()
}