package com.numq.stash.files

import com.numq.stash.loading.LoadingApi
import com.numq.stash.sharing.SharingApi
import com.numq.stash.wrapper.wrap

class FileData constructor(
    private val loadingService: LoadingApi,
    private val sharingService: SharingApi
) : FileRepository {

    override val events = sharingService.events.wrap()

    override fun clear() = sharingService.clear().wrap()

    override fun startSharing() = sharingService.startSharing().wrap()

    override fun stopSharing() = sharingService.stopSharing().wrap()

    override fun refresh() = sharingService.refresh().wrap()

    override fun shareFile(file: ImageFile) = sharingService.shareFile(file).wrap()

    override fun uploadFile(uri: String) =
        loadingService.upload(uri) { sharingService.shareFile(it) }.wrap()

    override fun downloadFile(file: ImageFile) = loadingService.downloadOne(file).wrap()

    override fun downloadMultipleFiles(files: List<ImageFile>) =
        loadingService.downloadMultiple(files).wrap()

    override fun downloadZip(files: List<ImageFile>) = loadingService.downloadZip(files).wrap()
}