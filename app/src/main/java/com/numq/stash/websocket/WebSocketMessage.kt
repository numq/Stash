package com.numq.stash.websocket

import org.json.JSONObject

data class WebSocketMessage(val type: String, val body: JSONObject = JSONObject()) {
    override fun toString() = JSONObject().apply {
        put(WebSocketConstants.TYPE, type)
        put(WebSocketConstants.BODY, body.toString())
    }.toString()
}