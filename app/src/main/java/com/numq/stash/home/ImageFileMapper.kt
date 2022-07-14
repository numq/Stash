package com.numq.stash.home

import com.numq.stash.websocket.SocketMessage
import org.json.JSONObject

val ImageFile.socketMessage: SocketMessage
    get() = SocketMessage("image", JSONObject().put("image", blob))
val SocketMessage.isImageFile: Boolean
    get() = body.has("image")
val SocketMessage.imageFile: ImageFile
    get() = ImageFile(body.getString("image"))