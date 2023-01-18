package com.numq.stash.websocket

import org.json.JSONObject

data class Message(val type: String, val body: JSONObject = JSONObject()) {
    companion object {
        const val TYPE = "type"
        const val BODY = "body"
        const val REFRESH = "refresh"
        const val UPLOAD = "upload"
        const val DELETE = "delete"
    }

    override fun toString() = JSONObject().apply {
        put(TYPE, type)
        put(BODY, body.toString())
    }.toString()
}