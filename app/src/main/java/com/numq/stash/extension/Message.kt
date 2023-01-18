package com.numq.stash.extension

import android.util.Base64
import com.numq.stash.file.DocumentFile
import com.numq.stash.file.File
import com.numq.stash.file.ImageFile
import com.numq.stash.websocket.Message

val Message.isFile: Boolean
    get() = runCatching { body.has(File.NAME) && body.has(File.EXTENSION) && body.has(File.BYTES) }.isSuccess

val Message.file: File
    get() = with(body) {
        val extension = getString(File.EXTENSION)
        when {
            ImageFile.extensions.contains(extension) -> ImageFile(
                getString(File.NAME),
                extension,
                Base64.decode(getString(File.BYTES), Base64.DEFAULT)
            )
            else -> DocumentFile(
                getString(File.NAME),
                extension,
                Base64.decode(getString(File.BYTES), Base64.DEFAULT)
            )
        }
    }