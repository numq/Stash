package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class SendFile constructor(private val repository: FileRepository) : UseCase<ImageFile, Boolean>() {
    override fun execute(arg: ImageFile) = repository.sendFile(arg)
}