package com.numq.stash.folder

import com.numq.stash.interactor.UseCase

class StartSharing constructor(
    private val repository: FolderRepository,
) : UseCase<String?, Unit>() {
    override suspend fun execute(arg: String?) = repository.startSharing(arg)
}