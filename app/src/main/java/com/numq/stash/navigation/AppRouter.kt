package com.numq.stash.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.numq.stash.files.FilesScreen
import com.numq.stash.permission.PermissionsRequired

@Composable
fun AppRouter() {

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val permissions = listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    PermissionsRequired(permissions) {
        Scaffold(scaffoldState = scaffoldState) {
            Box(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                NavHost(
                    navController,
                    startDestination = Route.Files.destination
                ) {
                    composable(Route.Files.destination) {
                        FilesScreen(scaffoldState, navController)
                    }
                }
            }
        }
    }
}