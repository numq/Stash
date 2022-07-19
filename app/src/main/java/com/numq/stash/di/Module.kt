package com.numq.stash.di

import android.app.Application
import android.app.DownloadManager
import com.numq.stash.files.*
import com.numq.stash.load.LoadApi
import com.numq.stash.load.LoadService
import com.numq.stash.notification.NotificationApi
import com.numq.stash.notification.NotificationService
import com.numq.stash.websocket.SocketApi
import com.numq.stash.websocket.SocketClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { androidContext().contentResolver }
    single { androidContext().getSystemService(Application.DOWNLOAD_SERVICE) as DownloadManager }
    single { SocketClient() } bind SocketApi::class
    single { FileService(get()) } bind FileApi::class
    single { NotificationService(androidContext()) } bind NotificationApi::class
    single { LoadService(androidContext(), get(), get()) } bind LoadApi::class
    single { FileData(get(), get()) } bind FileRepository::class
    single { StartSharing(get()) }
    single { StopSharing(get()) }
    single { GetFiles(get()) }
    single { Refresh(get()) }
    single { UploadFile(get()) }
    single { DownloadOneFile(get()) }
    single { DownloadMultipleFiles(get()) }
    single { DownloadZip(get()) }
    viewModel { FilesViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}