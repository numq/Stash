package com.numq.stash.files

import com.numq.stash.interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetFiles constructor(private val repository: FileRepository) : UseCase<Unit, Flow<ImageFile>>() {
    override fun execute(arg: Unit) = repository.files
}