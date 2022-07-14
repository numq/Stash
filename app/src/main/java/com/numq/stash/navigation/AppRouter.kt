package com.numq.stash.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.numq.stash.home.Home
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppRouter() {

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val permissions = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )
    )

    if (permissions.allPermissionsGranted) {
        Scaffold(scaffoldState = scaffoldState) {
            Box(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                NavHost(
                    navController,
                    startDestination = Route.Home.destination
                ) {
                    composable(Route.Home.destination) {
                        Home(scaffoldState)
                    }
                }
            }
        }
    } else {
        NoPermissions(permissions.permissions) {
            runBlocking {
                permissions.launchMultiplePermissionRequest()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoPermissions(permissions: List<PermissionState>, grant: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(8.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("You must grant these permissions:")
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(32.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                permissions.forEach {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (it.status.isGranted) Icons.Rounded.Done else Icons.Rounded.Close,
                            "",
                            tint = if (it.status.isGranted) Color.Green else Color.Red
                        )
                        Text(
                            it.permission.split(".").last(),
                            color = if (it.status.isGranted) Color.Green else Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(Modifier.height(64.dp))
            IconButton(onClick = { grant() }) {
                Icon(Icons.Rounded.Settings, "Open settings", modifier = Modifier.size(32.dp))
            }
        }
    }
}