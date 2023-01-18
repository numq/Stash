package com.numq.stash.file

import android.util.Base64
import arrow.core.Either
import com.numq.stash.connection.ConnectionException
import com.numq.stash.connection.ConnectionService
import com.numq.stash.extension.file
import com.numq.stash.extension.isFile
import com.numq.stash.extension.toEither
import com.numq.stash.websocket.Message
import com.numq.stash.websocket.SocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

interface FileRepository {

    val events: Either<Exception, Flow<FileEvent>>
    suspend fun refreshFiles(): Either<Exception, Unit>
    suspend fun shareFile(
        name: String,
        extension: String,
        bytes: ByteArray
    ): Either<Exception, Unit>

    suspend fun removeFile(file: File): Either<Exception, Unit>

    class Implementation constructor(
        private val connectionService: ConnectionService,
        private val client: SocketClient
    ) : FileRepository {

        override val events = runCatching {
            client.messages.consumeAsFlow().map {
                when (it.type) {
                    Message.REFRESH -> FileEvent.Refresh
                    Message.UPLOAD -> it.takeIf { it.isFile }
                        ?.let { message -> FileEvent.Upload(message.file) } ?: FileEvent.Empty
                    Message.DELETE -> it.takeIf { it.isFile }
                        ?.let { message -> FileEvent.Delete(message.file) } ?: FileEvent.Empty
                    else -> FileEvent.Empty
                }
            }
        }.toEither()

        override suspend fun refreshFiles() = runCatching {
            client.signal(Message(Message.REFRESH))
        }.toEither()

        override suspend fun shareFile(name: String, extension: String, bytes: ByteArray) =
            runCatching {
                client.signal(Message(Message.UPLOAD, JSONObject().apply {
                    put(File.NAME, name)
                    put(File.EXTENSION, extension)
                    put(File.BYTES, Base64.encodeToString(bytes, Base64.DEFAULT))
                }))
            }.toEither(connectionService.isConnected, ConnectionException)

        override suspend fun removeFile(file: File) = runCatching {
            client.signal(Message(Message.DELETE, JSONObject().apply {
                put(File.NAME, file.name)
                put(File.EXTENSION, file.extension)
                put(File.BYTES, Base64.encodeToString(file.bytes, Base64.DEFAULT))
            }))
        }.toEither(connectionService.isConnected, ConnectionException)
    }
}