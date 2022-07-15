package com.numq.stash.sharing

import com.numq.stash.websocket.SocketClient
import com.numq.stash.websocket.isSocketMessage
import com.numq.stash.websocket.socketMessage
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class FileService(private val client: SocketClient) : FileApi {

    companion object {
        const val REFRESH = "refresh"
        const val SEND = "image"
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

    override fun sendFile(imageFile: ImageFile) = client.signal(SEND, JSONObject().apply {
        put("image", imageFile.blob)
    })
}