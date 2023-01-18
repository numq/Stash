package com.numq.stash.file

import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.interactor.UseCase

class RefreshFiles constructor(
    private val repository: FileRepository
) : UseCase<Unit, CancellableAction>() {
    override suspend fun execute(arg: Unit) = repository.refreshFiles().action()
}