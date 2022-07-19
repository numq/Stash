package com.numq.stash.sharing

import com.numq.stash.extension.imageFile
import com.numq.stash.extension.isImageFile
import com.numq.stash.extension.isSocketMessage
import com.numq.stash.extension.webSocketMessage
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.WebSocketService
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class SharingService(private val webSocket: WebSocketService) : SharingApi {

    companion object {
        const val REFRESH = "refresh"
        const val SEND_IMAGE = "image"
    }

    override val files = webSocket.messages
        .consumeAsFlow()
        .filter { it.isSocketMessage }
        .map { it.webSocketMessage }
        .filter { it.isImageFile }
        .map { it.imageFile }

    override fun startSharing() = webSocket.connect()

    override fun stopSharing() = webSocket.disconnect()

    override fun refresh() = webSocket.signal(REFRESH)

    override fun shareFile(file: ImageFile) = webSocket.signal(SEND_IMAGE, JSONObject().apply {
        put(SEND_IMAGE, String(file.blob))
    })
}