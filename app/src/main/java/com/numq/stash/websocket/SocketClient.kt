package com.numq.stash.websocket

import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okio.ByteString
import org.json.JSONObject

class SocketClient : SocketApi {

    companion object {
        const val DEFAULT_URL = "ws://192.168.1.67:8080"
        const val DEFAULT_CODE = 1000
    }

    private val client = OkHttpClient.Builder().build()
    private val request = Request.Builder().url(DEFAULT_URL).build()

    private var socket: WebSocket? = null

    private val listener = object : DefaultSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            messages.trySend(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            messages.trySend(bytes.utf8())
        }
    }

    override val messages: Channel<String> = Channel(Channel.CONFLATED)

    override fun signal(type: String, body: JSONObject) {
        socket?.send(JSONObject().apply {
            put("type", type)
            put("body", body.toString())
        }.toString())
    }

    override fun connect() {
        socket = client.newWebSocket(request, listener)
    }

    override fun disconnect() {
        socket?.close(DEFAULT_CODE, "disconnect")
        socket = null
    }
}