package com.numq.stash.home

import arrow.core.left
import arrow.core.right

class FileData constructor(private val service: FileService) : FileRepository {

    override val files = runCatching { service.files }.fold({ it.right() },
        { Exception(it.localizedMessage).left() })

    override fun refresh() = runCatching { service.refresh() }.fold({ it.right() },
        { Exception(it.localizedMessage).left() })

    override fun sendFile(imageFile: ImageFile) =
        runCatching { service.sendFile(imageFile) }.fold({ it.right() },
            { Exception(it.localizedMessage).left() })
}