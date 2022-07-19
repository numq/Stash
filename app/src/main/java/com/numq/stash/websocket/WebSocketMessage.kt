package com.numq.stash.websocket

import org.json.JSONObject

data class WebSocketMessage(val type: String, val body: JSONObject = JSONObject())