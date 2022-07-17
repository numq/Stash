package com.numq.stash.websocket

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
import org.json.JSONObject

class SocketClient : SocketApi {

    private val coroutineContext = Dispatchers.Main + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    companion object {
        const val DEFAULT_URL = "ws://192.168.1.67:8080"
        const val DEFAULT_CODE = 1000
    }

    private fun createSocket(client: OkHttpClient, request: Request, listener: WebSocketListener) =
        client.newWebSocket(request, listener)

    private val client = OkHttpClient.Builder().build()
    private val request = Request.Builder().url(DEFAULT_URL).build()
    private val listener = object : DefaultSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            coroutineScope.launch {
                messages.send(text)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            coroutineScope.launch {
                messages.send(bytes.utf8())
            }
        }
    }

    private var socket: WebSocket? = null
    override val connected = socket != null
    override val messages: Channel<String> = Channel()

    override fun signal(type: String, body: JSONObject) = socket?.send(JSONObject().apply {
        put("type", type)
        put("body", body)
    }.toString()) == true

    override fun connect(): Boolean {
        socket = createSocket(client, request, listener)
        return true
    }

    override fun disconnect(): Boolean {
        socket?.close(DEFAULT_CODE, "disconnect")
        socket = null
        return false
    }
}