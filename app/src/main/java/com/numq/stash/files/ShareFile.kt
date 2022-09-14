package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class ShareFile constructor(private val repository: FileRepository) : UseCase<ImageFile, Boolean>() {
    override suspend fun execute(arg: ImageFile) = repository.shareFile(arg)
}