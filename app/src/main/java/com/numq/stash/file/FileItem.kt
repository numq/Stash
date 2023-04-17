package com.numq.stash.file

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numq.stash.extension.kindTitle

@Composable
fun FileItem(file: File) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(Modifier.aspectRatio(1f)) {
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f), contentAlignment = Alignment.Center) {
                when (file) {
                    is ImageFile -> {
                        runCatching {
                            BitmapFactory.decodeStream(file.bytes.inputStream()).asImageBitmap()
                        }.fold(onSuccess = {
                            val bitmap by remember { mutableStateOf(it) }
                            Image(
                                bitmap = bitmap,
                                contentDescription = "image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }, onFailure = {
                            Icon(Icons.Rounded.Error, "failed to load image")
                        })
                    }
                    else -> Text(file.extension, fontSize = 32.sp)
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(4.dp), contentAlignment = Alignment.Center
        ) {
            Text(file.kindTitle())
        }
    }
}