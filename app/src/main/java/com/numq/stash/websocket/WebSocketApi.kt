package com.numq.stash.websocket

import kotlinx.coroutines.channels.Channel

interface WebSocketApi {
    val messages: Channel<WebSocketMessage>
    fun signal(message: WebSocketMessage): Boolean
    fun connect(): Boolean
    fun disconnect(): Boolean
}