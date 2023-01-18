package com.numq.stash.folder

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import com.numq.stash.file.File

@Composable
fun SelectionContainer(
    file: File,
    isSelected: Boolean,
    onClick: (File) -> Unit,
    onLongClick: (File) -> Unit,
    content: @Composable () -> Unit
) {
    Card(Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            onClick(file)
        }, onLongPress = {
            onLongClick(file)
        })
    }) {
        Box(Modifier.alpha(if (isSelected) .5f else 1f), contentAlignment = Alignment.Center) {
            content()
        }
    }
}