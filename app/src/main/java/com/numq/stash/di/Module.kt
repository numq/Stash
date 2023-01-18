package com.numq.stash.di

import android.app.Application
import android.app.DownloadManager
import com.numq.stash.config.Configuration
import com.numq.stash.connection.ConnectionService
import com.numq.stash.file.*
import com.numq.stash.folder.*
import com.numq.stash.navigation.NavigationViewModel
import com.numq.stash.notification.NotificationService
import com.numq.stash.transfer.*
import com.numq.stash.websocket.SocketClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val context = module {
    single { androidContext().contentResolver }
    single { androidContext().getSystemService(Application.DOWNLOAD_SERVICE) as DownloadManager }
    single { ConnectionService(androidContext()) }
    single { NotificationService(androidContext()) }
}

val socket = module {
    val address = SocketClient.ADDRESS_PATTERN.format(
        Configuration.SOCKET_HOSTNAME,
        Configuration.SOCKET_PORT
    )
    single { SocketClient.Implementation(address) } bind SocketClient::class
}

val file = module {
    single { FileRepository.Implementation(get(), get()) } bind FileRepository::class
    factory { GetFileEvents(get()) }
    factory { RefreshFiles(get()) }
    factory { ShareFile(get()) }
    factory { RemoveFile(get()) }
}

val folder = module {
    single { FolderRepository.Implementation(get()) } bind FolderRepository::class
    factory { GetSharingStatus(get()) }
    factory { StartSharing(get()) }
    factory { StopSharing(get()) }
    viewModel {
        FolderViewModel(
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

val transfer = module {
    single { TransferService.Implementation(androidContext(), get()) } bind TransferService::class
    factory { GetTransferActions(get()) } bind GetTransferActions::class
    factory { RequestTransfer(get()) } bind RequestTransfer::class
    factory { UploadFile(get()) } bind UploadFile::class
    factory { DownloadFile(get()) } bind DownloadFile::class
    factory { DownloadZip(get()) } bind DownloadZip::class
}

val navigation = module {
    viewModel { NavigationViewModel(get(), get(), get(), get()) }
}

val appModule = context + socket + file + folder + transfer + navigation
