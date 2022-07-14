package com.numq.stash.home

import com.numq.stash.interactor.UseCase

class Refresh constructor(private val repository: FileRepository) : UseCase<Unit, Unit>() {
    override fun execute(arg: Unit) = repository.refresh()
}