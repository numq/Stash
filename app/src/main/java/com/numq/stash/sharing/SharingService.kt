package com.numq.stash.sharing

import com.numq.stash.extension.imageFile
import com.numq.stash.extension.isImageFile
import com.numq.stash.extension.isSocketMessage
import com.numq.stash.extension.socketMessage
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.SocketClient
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class SharingService(private val client: SocketClient) : SharingApi {

    companion object {
        const val REFRESH = "refresh"
        const val SEND_IMAGE = "image"
    }

    override val files = client.messages
        .consumeAsFlow()
        .filter { it.isSocketMessage }
        .map { it.socketMessage }
        .filter { it.isImageFile }
        .map { it.imageFile }

    override fun startSharing() = client.connect()

    override fun stopSharing() = client.disconnect()

    override fun refresh() = client.signal(REFRESH)

    override fun shareFile(file: ImageFile) = client.signal(SEND_IMAGE, JSONObject().apply {
        put(SEND_IMAGE, String(file.blob))
    })
}