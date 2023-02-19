package com.numq.stash.folder

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.numq.stash.extension.countSuffix
import com.numq.stash.extension.kindTitle
import com.numq.stash.file.FileItem
import com.numq.stash.file.FilePreviewItem
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.androidx.compose.getViewModel

@Composable
fun FolderScreen(onException: (Exception?) -> Unit) {

    val vm: FolderViewModel = getViewModel()

    onException(vm.exception.consumeAsFlow().collectAsState(null).value)

    val state by vm.state.collectAsState()

    val isSharing = state.sharingStatus == SharingStatus.SHARING

    val selectionMode = state.selectedFiles.isNotEmpty()

    Scaffold(floatingActionButton = {
        if (selectionMode) Row(
            Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card {
                Row(
                    Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = vm::exitSelection) {
                        Icon(Icons.Rounded.Cancel, "exit selection")
                    }
                    IconButton(onClick = vm::selectAll) {
                        Icon(Icons.Rounded.DoneAll, "select all")
                    }
                }
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center, bottomBar = {
        if (state.previewFile == null) {
            BottomAppBar {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectionMode) {
                        Text("${state.selectedFiles.count()} file${state.selectedFiles.countSuffix} selected")
                        IconButton(onClick = vm::removeSelectedFiles, enabled = isSharing) {
                            Icon(Icons.Rounded.DeleteForever, "remove selected files")
                        }
                        IconButton(onClick = vm::downloadSelectedFiles) {
                            Icon(Icons.Rounded.Download, "download selected files")
                        }
                        IconButton(onClick = vm::downloadSelectedFilesAsZip) {
                            Row(
                                Modifier.padding(start = 4.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ZIP", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        val statusText = when (state.sharingStatus) {
                            SharingStatus.OFFLINE -> "Offline"
                            SharingStatus.CONNECTING -> "Connecting"
                            SharingStatus.SHARING -> "${state.files.count()} file${state.files.countSuffix} found"
                        }
                        Text(statusText)
                        IconButton(onClick = vm::refreshFiles, enabled = isSharing) {
                            Icon(Icons.Rounded.Refresh, "refresh", modifier = Modifier.size(32.dp))
                        }
                        IconButton(onClick = vm::upload, enabled = isSharing) {
                            Icon(Icons.Rounded.UploadFile, "upload file")
                        }
                        when (state.sharingStatus) {
                            SharingStatus.SHARING -> {
                                IconButton(onClick = vm::stopSharing) {
                                    Icon(
                                        Icons.Rounded.CloudOff,
                                        "stop sharing",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            SharingStatus.CONNECTING -> {
                                val infiniteTransition = rememberInfiniteTransition()
                                val angle by infiniteTransition.animateFloat(
                                    initialValue = 0F,
                                    targetValue = 360F,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, easing = LinearEasing)
                                    )
                                )
                                IconButton(onClick = vm::stopSharing) {
                                    Icon(
                                        Icons.Rounded.Sync,
                                        "connecting",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .rotate(angle)
                                    )
                                }
                            }
                            SharingStatus.OFFLINE -> {
                                IconButton(onClick = vm::startSharing) {
                                    Icon(
                                        Icons.Rounded.Cloud,
                                        "start sharing",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.files, key = { it.name }) { file ->
                    if (selectionMode) {
                        SelectionContainer(
                            file,
                            state.selectedFiles.any { it.name == file.name },
                            vm::manageSelection,
                            vm::manageSelection
                        ) {
                            FileItem(file)
                        }
                    } else {
                        InteractionContainer(
                            file,
                            isSharing,
                            vm::openFile,
                            vm::enterSelection,
                            vm::removeFile,
                            vm::downloadFile
                        ) {
                            FileItem(file)
                        }
                    }
                }
            }
        }
        state.previewFile?.let { file ->

            BackHandler {
                vm.closeFile()
            }

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
                            IconButton(onClick = vm::closeFile) {
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
                                vm.removeFile(file)
                            }, enabled = isSharing) {
                                Icon(Icons.Rounded.DeleteForever, "remove file")
                            }
                            IconButton(onClick = {
                                vm.downloadFile(file)
                            }) {
                                Icon(Icons.Rounded.Save, "download file")
                            }
                        }
                    }
                }
            }, floatingActionButtonPosition = FabPosition.Center, floatingActionButton = {
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            vm.filterByExtension()
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter ${file.extension.uppercase()}")
                    Checkbox(state.filteredByExtension, onCheckedChange = {}, enabled = false)
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
                        vm.previousFile(file)
                    }, enabled = state.files.size > 1) {
                        Icon(Icons.Rounded.ArrowBack, "previous file")
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        FilePreviewItem(file)
                    }
                    IconButton(onClick = {
                        vm.nextFile(file)
                    }, enabled = state.files.size > 1) {
                        Icon(Icons.Rounded.ArrowForward, "next file")
                    }
                }
            }
        }
    }
}