package com.numq.stash.websocket

import kotlinx.coroutines.channels.Channel
import org.json.JSONObject

interface SocketApi {
    val messages: Channel<String>
    fun signal(type: String, body: JSONObject = JSONObject())
    fun connect()
    fun disconnect()
}