package com.numq.stash.sharing

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    val files: Either<Exception, Flow<ImageFile>>
    fun startSharing(): Either<Exception, Boolean>
    fun stopSharing(): Either<Exception, Boolean>
    fun refresh(): Either<Exception, Boolean>
    fun sendFile(imageFile: ImageFile): Either<Exception, Boolean>
}