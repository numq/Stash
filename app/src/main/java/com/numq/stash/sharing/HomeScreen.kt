package com.numq.stash.sharing

import android.util.Base64
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.numq.stash.home.ShowError
import org.koin.androidx.compose.getViewModel

@Composable
fun Home(scaffoldState: ScaffoldState, vm: HomeViewModel = getViewModel()) {
    val state by vm.state.collectAsState()
    val (currentIndex, setCurrentIndex) = remember {
        mutableStateOf(-1)
    }
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
            state.exception?.toString()?.let {
                Text(it, color = Color.Red)
            }
            if (state.isSharing) {
                Text("Found ${state.imageFiles.count()} files", color = Color.Green)
                IconButton(onClick = { vm.refresh() }) {
                    Icon(Icons.Rounded.Refresh, "refresh", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = { vm.stopSharing() }) {
                    Icon(Icons.Rounded.Cloud, "stop sharing", modifier = Modifier.size(32.dp))
                }
            } else {
                IconButton(onClick = { vm.startSharing() }) {
                    Icon(Icons.Rounded.CloudOff, "start sharing", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun ImageFileItem(file: ImageFile, onClick: (ImageFile) -> Unit) {
    AsyncImage(
        model = Base64.decode(file.blob.split(",")[1], Base64.DEFAULT),
        contentDescription = "shared image",
        Modifier
            .fillMaxWidth()
            .clickable { onClick(file) }
    )
}