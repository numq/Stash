package com.numq.stash.sharing

import com.numq.stash.interactor.UseCase

class Refresh constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override fun execute(arg: Unit) = repository.refresh()
}