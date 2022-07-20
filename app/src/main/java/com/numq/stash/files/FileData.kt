package com.numq.stash.files

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.numq.stash.loading.LoadingApi
import com.numq.stash.sharing.SharingApi

class FileData constructor(
    private val loadingService: LoadingApi,
    private val sharingService: SharingApi
) : FileRepository {

    private fun <T> T.wrap(): Either<Exception, T> = runCatching { this }.fold({ it.right() },
        { Exception(it.localizedMessage).left() })

    override val events = sharingService.events.wrap()

    override fun clear() = sharingService.clear().wrap()

    override fun startSharing() = sharingService.startSharing().wrap()

    override fun stopSharing() = sharingService.stopSharing().wrap()

    override fun refresh() = sharingService.refresh().wrap()

    override fun sendFile(file: ImageFile) = sharingService.shareFile(file).wrap()

    override fun uploadFile(uri: String) =
        loadingService.upload(uri) { sharingService.shareFile(it) }.wrap()

    override fun downloadFile(file: ImageFile) = loadingService.downloadOne(file).wrap()

    override fun downloadMultipleFiles(files: List<ImageFile>) =
        loadingService.downloadMultiple(files).wrap()

    override fun downloadZip(files: List<ImageFile>) = loadingService.downloadZip(files).wrap()
}