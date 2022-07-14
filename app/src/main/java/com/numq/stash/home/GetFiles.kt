package com.numq.stash.home

import com.numq.stash.interactor.UseCase
import kotlinx.coroutines.channels.Channel

class GetFiles constructor(private val repository: FileRepository) : UseCase<Unit, Channel<ImageFile>>() {
    override fun execute(arg: Unit) = repository.files
}