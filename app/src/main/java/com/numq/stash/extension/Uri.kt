package com.numq.stash.extension

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log

fun Uri.fileName(context: Context) =
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).coerceAtLeast(0)
        )
        else null
    }?.also {
        Log.d("fileName", it)
    }