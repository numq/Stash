package com.numq.stash.transfer

import android.content.Context
import android.net.Uri
import arrow.core.Either
import com.numq.stash.extension.catch
import com.numq.stash.extension.catchAsync
import com.numq.stash.file.File
import com.numq.stash.file.ImageFile
import com.numq.stash.notification.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface TransferService {
    val actions: Either<Exception, Channel<TransferAction>>
    suspend fun requestTransfer(event: TransferAction): Either<Exception, Unit>
    suspend fun downloadFile(uri: String, file: File): Either<Exception, Unit>
    suspend fun downloadZip(uri: String, files: List<File>): Either<Exception, Unit>

    class Implementation constructor(
        private val context: Context,
        private val notification: NotificationService
    ) : TransferService {

        override val actions = catch {
            Channel<TransferAction>(Channel.BUFFERED)
        }

        override suspend fun requestTransfer(event: TransferAction) = catch {
            actions.orNull()?.trySend(event)?.getOrThrow() ?: Unit
        }

        override suspend fun downloadFile(uri: String, file: File) = catchAsync(Dispatchers.IO) {
            Uri.parse(uri)?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(file.bytes)
                }
            }?.also {
                val mimeType = when (file) {
                    is ImageFile -> "image/${file.extension}"
                    else -> "*/*"
                }
                notification.showDownloadNotification(uri, mimeType)
            } ?: Unit
        }

        override suspend fun downloadZip(uri: String, files: List<File>) =
            catchAsync(Dispatchers.IO) {
                Uri.parse(uri)?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use {
                        ZipOutputStream(it).use { zip ->
                            files.forEach { file ->
                                zip.putNextEntry(ZipEntry("${file.name}.${file.extension}"))
                                zip.write(file.bytes)
                            }
                        }
                    }
                }?.also {
                    notification.showDownloadNotification(uri, "application/zip")
                } ?: Unit
            }
    }
}