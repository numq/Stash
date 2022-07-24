package com.numq.stash.extension

import com.numq.stash.websocket.WebSocketConstants
import com.numq.stash.websocket.WebSocketMessage
import org.json.JSONObject

val String.isSocketMessage: Boolean
    get() = runCatching {
        with(JSONObject(this)) {
            has(WebSocketConstants.TYPE) && has(WebSocketConstants.BODY)
        }
    }.isSuccess

val String.webSocketMessage: WebSocketMessage
    get() = with(JSONObject(this)) {
        WebSocketMessage(
            getString(WebSocketConstants.TYPE),
            JSONObject(getString(WebSocketConstants.BODY))
        )
    }