package com.numq.stash.folder.container

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.numq.stash.file.File

@Composable
fun InteractionContainer(
    file: File,
    isSharing: Boolean,
    onClick: (File) -> Unit,
    onLongClick: (File) -> Unit,
    onRemove: (File) -> Unit,
    onSave: (File) -> Unit,
    content: @Composable () -> Unit
) {
    Card(Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            onClick(file)
        }, onLongPress = {
            onLongClick(file)
        })
    }) {
        Box(contentAlignment = Alignment.TopCenter) {
            content()
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onRemove(file)
                }, enabled = isSharing) {
                    Icon(Icons.Rounded.Delete, "delete file")
                }
                IconButton(onClick = {
                    onSave(file)
                }) {
                    Icon(Icons.Rounded.Save, "save file")
                }
            }
        }
    }
}