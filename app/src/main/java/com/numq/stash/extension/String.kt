package com.numq.stash.extension

import com.numq.stash.websocket.Message
import org.json.JSONObject

val String.isSocketMessage: Boolean
    get() = runCatching {
        with(JSONObject(this)) {
            has(Message.TYPE) && has(Message.BODY)
        }
    }.isSuccess

val String.message: Message
    get() = with(JSONObject(this)) {
        Message(getString(Message.TYPE), JSONObject(getString(Message.BODY)))
    }