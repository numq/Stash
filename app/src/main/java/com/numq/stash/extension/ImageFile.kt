package com.numq.stash.extension

import android.util.Base64
import android.util.Log
import com.numq.stash.files.ImageFile

val ImageFile.base64: ByteArray?
    get() = runCatching {
        Base64.decode(blob, Base64.DEFAULT)
    }.onFailure { it.localizedMessage?.let { e -> Log.e(javaClass.simpleName, e) } }.getOrNull()