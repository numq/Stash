package com.numq.stash.file

import com.numq.stash.interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetFileEvents constructor(
    private val repository: FileRepository
) : UseCase<Unit, Flow<FileEvent>>() {
    override suspend fun execute(arg: Unit) = repository.events
}