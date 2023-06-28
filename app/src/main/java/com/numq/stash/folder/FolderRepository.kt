package com.numq.stash.folder

import arrow.core.Either
import com.numq.stash.extension.catch
import com.numq.stash.extension.catchAsync
import com.numq.stash.websocket.ConnectionState
import com.numq.stash.websocket.SocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FolderRepository {

    val sharingState: Either<Exception, Flow<SharingStatus>>
    suspend fun startSharing(address: String?): Either<Exception, Unit>
    suspend fun stopSharing(): Either<Exception, Unit>

    class Implementation constructor(
        private val client: SocketClient
    ) : FolderRepository {

        override val sharingState = catch {
            client.connectionState.map { connection ->
                when (connection) {
                    is ConnectionState.Disconnected -> SharingStatus.Offline
                    is ConnectionState.Connecting -> SharingStatus.Connecting
                    is ConnectionState.Connected -> SharingStatus.Sharing(connection.address.toString())
                }
            }
        }

        override suspend fun startSharing(address: String?) = catchAsync { client.startWithString(address) }

        override suspend fun stopSharing() = catchAsync { client.stop() }
    }
}