package com.numq.stash.extension

import com.numq.stash.files.ImageFile
import com.numq.stash.websocket.WebSocketConstants
import com.numq.stash.websocket.WebSocketMessage

val WebSocketMessage.isImageFile: Boolean
    get() = runCatching {
        body.has(WebSocketConstants.FILE_EXTENSION) && body.has(WebSocketConstants.FILE_BLOB)
    }.isSuccess

val WebSocketMessage.imageFile: ImageFile
    get() = with(body) {
        ImageFile(
            getString(WebSocketConstants.FILE_EXTENSION),
            getString(WebSocketConstants.FILE_BLOB)
        )
    }