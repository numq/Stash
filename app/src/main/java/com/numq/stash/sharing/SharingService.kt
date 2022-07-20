package com.numq.stash.sharing

import com.numq.stash.extension.imageFile
import com.numq.stash.extension.isImageFile
import com.numq.stash.files.FileEvent
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.WebSocketService
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class SharingService(private val webSocket: WebSocketService) : SharingApi {

    companion object {
        const val CLEAR = "clear"
        const val REFRESH = "refresh"
        const val IMAGE = "image"
    }

    override val events = webSocket.messages.consumeAsFlow().map {
        when (it.type) {
            CLEAR -> FileEvent.Clear
            REFRESH -> FileEvent.Refresh
            IMAGE -> if (it.isImageFile) FileEvent.File(it.imageFile) else FileEvent.Empty
            else -> FileEvent.Empty
        }
    }

    override fun clear() = webSocket.signal(CLEAR)

    override fun refresh() = webSocket.signal(REFRESH)

    override fun startSharing() = webSocket.connect()

    override fun stopSharing() = webSocket.disconnect()

    override fun shareFile(file: ImageFile) = webSocket.signal(IMAGE, JSONObject().apply {
        put(IMAGE, String(file.blob))
    })
}