package com.numq.stash.extension

import android.util.Base64
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.WebSocketMessage

val WebSocketMessage.isImageFile: Boolean
    get() = body.has("image") && body.getString("image").startsWith("data:")
val WebSocketMessage.imageFile: ImageFile
    get() = with(body.getString("image")) {
        ImageFile(
            split("/")[1].split(";")[0],
            Base64.decode(split(",")[1], Base64.DEFAULT)
        )
    }