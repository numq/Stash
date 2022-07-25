package com.numq.stash.sharing

import com.numq.stash.extension.imageFile
import com.numq.stash.extension.isImageFile
import com.numq.stash.files.FileEvent
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.WebSocketConstants
import com.numq.stash.websocket.WebSocketMessage
import com.numq.stash.websocket.WebSocketService
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class SharingService(private val webSocket: WebSocketService) : SharingApi {

    override val events = webSocket.messages.consumeAsFlow().map {
        when (it.type) {
            WebSocketConstants.CLEAR -> FileEvent.Clear
            WebSocketConstants.REFRESH -> FileEvent.Refresh
            WebSocketConstants.FILE -> if (it.isImageFile) FileEvent.File(it.imageFile) else FileEvent.Empty
            else -> FileEvent.Empty
        }
    }

    override fun clear() = webSocket.signal(WebSocketMessage(WebSocketConstants.CLEAR))

    override fun refresh() = webSocket.signal(WebSocketMessage(WebSocketConstants.REFRESH))

    override fun startSharing(): Boolean {
        webSocket.connect()
        return true
    }

    override fun stopSharing(): Boolean {
        webSocket.disconnect()
        return false
    }

    override fun shareFile(file: ImageFile) =
        webSocket.signal(WebSocketMessage(WebSocketConstants.FILE, JSONObject().apply {
            put(WebSocketConstants.FILE_EXTENSION, file.extension)
            put(WebSocketConstants.FILE_BLOB, file.blob)
        }))
}