package com.numq.stash.extension

import com.numq.stash.websocket.SocketMessage
import org.json.JSONObject

val String.isSocketMessage: Boolean
    get() = with(JSONObject(this)) {
        has("type") && has("body")
    }
val String.socketMessage: SocketMessage
    get() = with(JSONObject(this)) {
        SocketMessage(getString("type"), JSONObject(getString("body")))
    }