package com.numq.stash.files

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class FileData constructor(
    private val loader: LoadApi,
    private val service: FileApi
) : FileRepository {

    private fun <T> T.wrap(): Either<Exception, T> = runCatching { this }.fold({ it.right() },
        { Exception(it.localizedMessage).left() })

    override val files = service.files.wrap()

    override fun startSharing() = service.startSharing().wrap()

    override fun stopSharing() = service.stopSharing().wrap()

    override fun refresh() = service.refresh().wrap()

    override fun uploadFile(uri: String) = loader.upload(uri) { service.sendFile(it) }.wrap()

    override fun downloadFile(file: ImageFile) = loader.downloadOne(file).wrap()

    override fun downloadMultipleFiles(files: List<ImageFile>) = loader.downloadMultiple(files).wrap()

    override fun downloadZip(files: List<ImageFile>) = loader.downloadZip(files).wrap()
}