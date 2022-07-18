package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class StopSharing constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override fun execute(arg: Unit) = repository.stopSharing()
}