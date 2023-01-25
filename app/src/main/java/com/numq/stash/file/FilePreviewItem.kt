package com.numq.stash.file

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.sp

@Composable
fun FilePreviewItem(file: File) {
    when (file) {
        is ImageFile -> {
            runCatching {
                BitmapFactory.decodeStream(file.bytes.inputStream()).asImageBitmap()
            }.fold(onSuccess = {
                Image(
                    bitmap = it,
                    contentDescription = "image",
                    modifier = Modifier.fillMaxHeight()
                )
            }, onFailure = {
                Icon(Icons.Rounded.Error, "failed to load image")
            })
        }
        else -> Text(file.extension, fontSize = 32.sp)
    }
}