package com.numq.stash.home

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import org.koin.androidx.compose.getViewModel

@Composable
fun Home(scaffoldState: ScaffoldState, vm: HomeViewModel = getViewModel()) {
    val state by vm.state.collectAsState()
    state.exception?.let {
        ShowError(scaffoldState, it)
    }
    LaunchedEffect(Unit) {
        vm.refresh()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${state.imageFiles.count()}", color = Color.Green)
            state.exception?.toString()?.let {
                Text(it, color = Color.Red)
            }
            IconButton(onClick = { vm.refresh() }) {
                Icon(Icons.Rounded.Refresh, "refresh")
            }
        }
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2), modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.imageFiles) { file ->
                ImageFileItem(file)
            }
        }
    }
}

@Composable
fun ImageFileItem(file: ImageFile) {
    Card {
        Image(bitmap = with(Base64.decode(file.blob, Base64.DEFAULT)) {
            BitmapFactory.decodeByteArray(this, 0, size - 1)
        }.asImageBitmap(), contentDescription = "shared image", Modifier.fillMaxWidth())
    }
}