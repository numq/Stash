package com.numq.stash.files

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.numq.stash.extension.base64
import com.numq.stash.error.ShowError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun FilesScreen(
    scaffoldState: ScaffoldState,
    navController: NavController,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    vm: FileViewModel = getViewModel()
) {

    val state by vm.state.collectAsState()

    val (currentIndex, setCurrentIndex) = remember {
        mutableStateOf(-1)
    }

    val uploadLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
            it.forEach { uri ->
                vm.uploadFile(uri.toString())
            }
        }

    val uploadFile: () -> Unit = { uploadLauncher.launch(arrayOf("image/*")) }

    state.exception?.let {
        ShowError(scaffoldState, it)
    }

    LaunchedEffect(state.imageFiles) {
        if (currentIndex < 0 && state.imageFiles.isNotEmpty()) setCurrentIndex(0)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.imageFiles) { file ->
                ImageFileItem(file) {
                    vm.downloadOneFile(it)
                }
            }
        }
        if (state.imageFiles.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        onClick = {
                            vm.downloadMultipleFiles(state.imageFiles)
                        }) {
                        Text("DOWNLOAD ALL")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        onClick = {
                            vm.downloadZip(state.imageFiles)
                        }) {
                        Text("DOWNLOAD ZIP")
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                    onClick = {
                        coroutineScope.launch {
                            when (scaffoldState.snackbarHostState.showSnackbar(
                                "Do you want to clear all?",
                                "Yes, clear all",
                                SnackbarDuration.Short
                            )) {
                                SnackbarResult.ActionPerformed -> vm.clearFiles()
                                else -> Unit
                            }
                        }
                    }) {
                    Text("CLEAR ALL")
                }
            }
        }
        Divider()
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isSharing) {
                Text("Found ${state.imageFiles.count()} files", color = Color.Green)
                IconButton(onClick = { vm.refresh() }) {
                    Icon(Icons.Rounded.Refresh, "refresh", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = uploadFile) {
                    Icon(Icons.Rounded.UploadFile, "upload file")
                }
                IconButton(onClick = { vm.stopSharing() }) {
                    Icon(Icons.Rounded.CloudOff, "stop sharing", modifier = Modifier.size(32.dp))
                }
            } else {
                IconButton(onClick = { vm.startSharing() }) {
                    Icon(Icons.Rounded.Cloud, "start sharing", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun ImageFileItem(file: ImageFile, onClick: (ImageFile) -> Unit) {
    AsyncImage(
        model = file.base64,
        contentDescription = "shared image",
        Modifier
            .fillMaxWidth()
            .clickable { onClick(file) }
    )
}