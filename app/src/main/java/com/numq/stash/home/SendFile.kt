package com.numq.stash.home

import com.numq.stash.interactor.UseCase

class SendFile constructor(private val repository: FileRepository) : UseCase<ImageFile, Unit>() {
    override fun execute(arg: ImageFile) = repository.sendFile(arg)
}