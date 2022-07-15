package com.numq.stash.sharing

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class FileData constructor(private val service: FileApi) : FileRepository {

    private fun <T> T.wrap(): Either<Exception, T> = runCatching { this }.fold({ it.right() },
        { Exception(it.localizedMessage).left() })

    override val files = service.files.wrap()

    override fun startSharing() = service.startSharing().wrap()

    override fun stopSharing() = service.stopSharing().wrap()

    override fun refresh() = service.refresh().wrap()

    override fun sendFile(imageFile: ImageFile) = service.sendFile(imageFile).wrap()
}