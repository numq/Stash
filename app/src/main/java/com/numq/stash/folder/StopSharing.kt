package com.numq.stash.folder

import com.numq.stash.interactor.UseCase

class StopSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, Unit>() {
    override suspend fun execute(arg: Unit) = repository.stopSharing()
}