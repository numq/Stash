package com.numq.stash.folder

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.numq.stash.extension.kindTitle
import com.numq.stash.file.File
import com.numq.stash.file.FileItemPreview

@Composable
fun PreviewFile(
    file: File,
    isSharing: Boolean,
    controlsVisible: Boolean,
    filteredByExtension: Boolean,
    filterByExtension: () -> Unit,
    previousFile: (File) -> Unit,
    nextFile: (File) -> Unit,
    downloadFile: (File) -> Unit,
    removeFile: (File) -> Unit,
    closeFile: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = closeFile) {
                        Icon(Icons.Rounded.KeyboardReturn, "close file")
                    }
                    Text(file.kindTitle())
                }
                Row(
                    Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        removeFile(file)
                    }, enabled = isSharing) {
                        Icon(Icons.Rounded.DeleteForever, "remove file")
                    }
                    IconButton(onClick = {
                        downloadFile(file)
                    }) {
                        Icon(Icons.Rounded.Save, "download file")
                    }
                }
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center, floatingActionButton = {
        Row(
            Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter ${file.extension.uppercase()}")
            Checkbox(filteredByExtension, onCheckedChange = { filterByExtension() })
        }
    }) { paddingValues ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                previousFile(file)
            }, enabled = controlsVisible) {
                Icon(Icons.Rounded.ArrowBack, "previous file")
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FileItemPreview(file)
            }
            IconButton(onClick = {
                nextFile(file)
            }, enabled = controlsVisible) {
                Icon(Icons.Rounded.ArrowForward, "next file")
            }
        }
    }
}