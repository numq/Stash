package com.numq.stash.extension

import android.util.Base64
import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.SocketMessage

val SocketMessage.isImageFile: Boolean
    get() = body.has("image") && body.getString("image").startsWith("data:")
val SocketMessage.imageFile: ImageFile
    get() = ImageFile(
        Base64.decode(
            body.getString("image").split(",")[1],
            Base64.DEFAULT
        )
    )