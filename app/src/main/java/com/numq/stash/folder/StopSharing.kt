package com.numq.stash.folder

import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.interactor.UseCase

class StopSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, CancellableAction>() {
    override suspend fun execute(arg: Unit) = repository.stopSharing().action()
}