package com.numq.stash.transfer

import arrow.core.flatMap
import arrow.core.right
import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.file.File
import com.numq.stash.interactor.UseCase

class DownloadZip constructor(
    private val service: TransferService
) : UseCase<Pair<String, List<File>>, CancellableAction>() {
    override suspend fun execute(arg: Pair<String, List<File>>) = arg.right()
        .flatMap { (uri, files) -> service.downloadZip(uri, files).action() }
}