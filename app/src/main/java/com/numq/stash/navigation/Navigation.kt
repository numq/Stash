package com.numq.stash.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.numq.stash.error.ErrorMessage
import com.numq.stash.extension.fileName
import com.numq.stash.folder.FolderScreen
import com.numq.stash.permission.PermissionsRequired
import com.numq.stash.transfer.TransferAction
import org.koin.androidx.compose.getViewModel

@Composable
fun Navigation() {

    val context = LocalContext.current
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    val permissions = listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val vm: NavigationViewModel = getViewModel()

    vm.exception.collectAsState(null).value?.let { ErrorMessage(scaffoldState, it) }

    val state by vm.state.collectAsState()

    val uploadLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        try {
            vm.uploadFiles(list.mapNotNull { uri ->
                context.contentResolver.openInputStream(uri)?.use {
                    val fileName = uri.fileName(context)
                    val name = fileName?.substringBeforeLast(".")
                    val extension = fileName?.substringAfterLast(".")
                    if (name != null && extension != null) {
                        Triple(name, extension, it.readBytes())
                    } else null
                }
            })
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e("Upload launcher", it) }
        } finally {
            vm.completeAction()
        }
    }

    val downloadLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument()
    ) { uri ->
        try {
            uri?.let {
                state.action?.run {
                    when (this) {
                        is TransferAction.DownloadFile -> vm.downloadFile(uri.toString(), file)
                        is TransferAction.DownloadZip -> vm.downloadZip(uri.toString(), files)
                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e("Download launcher", it) }
        } finally {
            vm.completeAction()
        }
    }

    state.action?.run {
        when (this) {
            is TransferAction.Upload -> uploadLauncher.launch(arrayOf("*/*"))
            is TransferAction.DownloadFile -> {
                downloadLauncher.launch("${file.name}.${file.extension}")
            }
            is TransferAction.DownloadZip -> {
                downloadLauncher.launch("${System.currentTimeMillis()}.zip")
            }
        }
    }

    PermissionsRequired(permissions) {
        Scaffold(scaffoldState = scaffoldState, modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController,
                startDestination = Destination.Folder.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                composable(Destination.Folder.name) {
                    FolderScreen(vm.onException)
                }
            }
        }
    }

}