package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class UploadFile constructor(private val repository: FileRepository) : UseCase<String, Boolean>() {
    override fun execute(arg: String) = repository.uploadFile(arg)
}