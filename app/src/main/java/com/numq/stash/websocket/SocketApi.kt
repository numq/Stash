package com.numq.stash.websocket

import kotlinx.coroutines.channels.Channel
import org.json.JSONObject

interface SocketApi {
    val connected: Boolean
    val messages: Channel<String>
    fun signal(type: String, body: JSONObject = JSONObject()): Boolean
    fun connect(): Boolean
    fun disconnect(): Boolean
}