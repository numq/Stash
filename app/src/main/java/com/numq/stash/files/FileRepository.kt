package com.numq.stash.files

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    val files: Either<Exception, Flow<ImageFile>>
    fun startSharing(): Either<Exception, Boolean>
    fun stopSharing(): Either<Exception, Boolean>
    fun refresh(): Either<Exception, Boolean>
    fun uploadFile(uri: String): Either<Exception, Boolean>
    fun downloadFile(file: ImageFile): Either<Exception, Boolean>
    fun downloadMultipleFiles(files: List<ImageFile>): Either<Exception, Boolean>
    fun downloadZip(files: List<ImageFile>): Either<Exception, Boolean>
}