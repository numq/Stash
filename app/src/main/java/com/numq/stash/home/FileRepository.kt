package com.numq.stash.home

import arrow.core.Either
import kotlinx.coroutines.channels.Channel

interface FileRepository {
    val files: Either<Exception, Channel<ImageFile>>
    fun refresh(): Either<Exception, Unit>
    fun sendFile(imageFile: ImageFile): Either<Exception, Unit>
}