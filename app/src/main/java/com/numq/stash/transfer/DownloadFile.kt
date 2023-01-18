package com.numq.stash.transfer

import arrow.core.flatMap
import arrow.core.right
import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.file.File
import com.numq.stash.interactor.UseCase

class DownloadFile constructor(
    private val service: TransferService
) : UseCase<Pair<String, File>, CancellableAction>() {
    override suspend fun execute(arg: Pair<String, File>) =
        arg.right().flatMap { (uri, file) -> service.downloadFile(uri, file).action() }
}