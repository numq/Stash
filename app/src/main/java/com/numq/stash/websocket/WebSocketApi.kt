package com.numq.stash.websocket

import kotlinx.coroutines.channels.Channel
import org.json.JSONObject

interface WebSocketApi {
    val messages: Channel<WebSocketMessage>
    fun signal(type: String, body: JSONObject = JSONObject()): Boolean
    fun connect(): Boolean
    fun disconnect(): Boolean
}