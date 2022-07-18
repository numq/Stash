package com.numq.stash.files

import com.numq.stash.interactor.UseCase

class DownloadZip constructor(private val repository: FileRepository) : UseCase<List<ImageFile>, Boolean>() {
    override fun execute(arg: List<ImageFile>) = repository.downloadZip(arg)
}