package com.numq.stash.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequired(permissions: List<String>, content: @Composable () -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(permissions)
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    if (permissionsState.allPermissionsGranted) content()
    else if (permissionsState.permissionRequested) Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(Modifier.clickable {
            permissionsState.launchMultiplePermissionRequest()
        }) {
            Column(
                Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Rounded.Replay, "try again")
                Text("Grant permissions")
            }
        }
    }
}