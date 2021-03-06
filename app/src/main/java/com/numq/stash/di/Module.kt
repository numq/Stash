package com.numq.stash.di

import android.app.Application
import android.app.DownloadManager
import com.numq.stash.files.*
import com.numq.stash.loading.LoadingApi
import com.numq.stash.loading.LoadingService
import com.numq.stash.notification.NotificationApi
import com.numq.stash.notification.NotificationService
import com.numq.stash.sharing.SharingApi
import com.numq.stash.sharing.SharingService
import com.numq.stash.websocket.WebSocketApi
import com.numq.stash.websocket.WebSocketService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { androidContext().contentResolver }
    single { androidContext().getSystemService(Application.DOWNLOAD_SERVICE) as DownloadManager }
    single { WebSocketService() } bind WebSocketApi::class
    single { SharingService(get()) } bind SharingApi::class
    single { NotificationService(androidContext()) } bind NotificationApi::class
    single { LoadingService(androidContext(), get(), get()) } bind LoadingApi::class
    single { FileData(get(), get()) } bind FileRepository::class
    factory { ClearFiles(get()) }
    factory { StartSharing(get()) }
    factory { StopSharing(get()) }
    factory { GetEvents(get()) }
    factory { Refresh(get()) }
    factory { ShareFile(get()) }
    factory { UploadFile(get()) }
    factory { DownloadOneFile(get()) }
    factory { DownloadMultipleFiles(get()) }
    factory { DownloadZip(get()) }
    viewModel {
        FileViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}