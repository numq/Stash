package com.numq.stash.files

import com.numq.stash.interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetEvents constructor(private val repository: FileRepository) : UseCase<Unit, Flow<FileEvent>>() {
    override fun execute(arg: Unit) = repository.events
}