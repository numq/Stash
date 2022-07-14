package com.numq.stash.websocket

import org.json.JSONObject

data class SocketMessage(val type: String, val body: JSONObject = JSONObject())