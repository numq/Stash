package com.numq.stash.extension

import com.numq.stash.websocket.WebSocketMessage
import org.json.JSONObject

val String.isSocketMessage: Boolean
    get() = with(JSONObject(this)) {
        has("type") && has("body")
    }
val String.webSocketMessage: WebSocketMessage
    get() = with(JSONObject(this)) {
        WebSocketMessage(getString("type"), JSONObject(getString("body")))
    }