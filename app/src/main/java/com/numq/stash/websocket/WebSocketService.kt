package com.numq.stash.websocket

import com.numq.stash.extension.isSocketMessage
import com.numq.stash.extension.webSocketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketService : WebSocketApi {

    companion object {
        const val REASON_DISCONNECT = "disconnect"
    }

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    private fun createSocket(client: OkHttpClient, request: Request, listener: WebSocketListener) =
        client.newWebSocket(request, listener)

    private val client = OkHttpClient.Builder().build()
    private val request = Request.Builder().url(WebSocketConfig.DEFAULT_URL).build()
    private val listener = object : DefaultWebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            coroutineScope.launch {
                with(text) {
                    if (isSocketMessage) messages.send(webSocketMessage)
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            coroutineScope.launch {
                with(bytes.utf8()) {
                    if (isSocketMessage) messages.send(webSocketMessage)
                }
            }
        }
    }

    private var socket: WebSocket? = null
    override val messages: Channel<WebSocketMessage> = Channel()

    override fun signal(message: WebSocketMessage) = runCatching {
        socket?.send(message.toString())
    }.isSuccess

    override fun connect() = runCatching {
        socket = createSocket(client, request, listener)
    }.isSuccess

    override fun disconnect() = runCatching {
        socket?.close(WebSocketConfig.DEFAULT_CODE, REASON_DISCONNECT)
        socket = null
    }.isSuccess
}