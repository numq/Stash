package com.numq.stash.home

import com.numq.stash.websocket.SocketClient
import com.numq.stash.websocket.isSocketMessage
import com.numq.stash.websocket.socketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject

class FileService(private val socket: SocketClient) : FileApi {

    companion object {
        const val REFRESH = "refresh"
        const val SEND = "image"
    }

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    init {
        coroutineScope.launch {
            socket.messages
                .consumeAsFlow()
                .filter { it.isSocketMessage }
                .map { it.socketMessage }
                .filter { it.isImageFile }
                .map { it.imageFile }
                .collect {
                    files.send(it)
                }
        }
    }

    override val files = Channel<ImageFile>()

    override fun refresh() = socket.signal(REFRESH)

    override fun sendFile(imageFile: ImageFile) = socket.signal(SEND, JSONObject().apply {
        put("image", imageFile.blob)
    })
}