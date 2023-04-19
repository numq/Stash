package com.numq.stash.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    vm.exception.collectAsStateWithLifecycle(null).value?.let { ErrorMessage(scaffoldState, it) }

    val state by vm.state.collectAsStateWithLifecycle()

    @Composable
    fun uploadFile(action: (List<Uri>) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
            onResult = {
                try {
                    it.takeIf { it.isNotEmpty() }?.let(action)
                        ?: throw Exception("Unable to upload file")
                } catch (e: Exception) {
                    vm.onException(e)
                } finally {
                    vm.completeAction()
                }
            }
        )
        LaunchedEffect(Unit) {
            launcher.launch(arrayOf("*/*"))
        }
    }

    @Composable
    fun downloadFile(name: String, extension: String, action: (Uri) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(),
            onResult = {
                try {
                    it?.let(action) ?: throw Exception("Unable to download file")
                } catch (e: Exception) {
                    vm.onException(e)
                } finally {
                    vm.completeAction()
                }
            }
        )
        LaunchedEffect(Unit) {
            launcher.launch("$name.$extension")
        }
    }

    state.action?.run {
        when (this) {
            is TransferAction.Upload -> uploadFile { list ->
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
            }
            is TransferAction.DownloadFile -> downloadFile(file.name, file.extension) { uri ->
                vm.downloadFile(uri.toString(), file)
            }
            is TransferAction.DownloadMultipleFiles -> files.forEach { file ->
                downloadFile(file.name, file.extension) { uri ->
                    vm.downloadFile(uri.toString(), file)
                }
            }
            is TransferAction.DownloadZip -> downloadFile(
                "${System.currentTimeMillis()}",
                "zip"
            ) { uri ->
                vm.downloadZip(uri.toString(), files)
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