package com.numq.stash.di

import com.numq.stash.home.*
import com.numq.stash.websocket.SocketApi
import com.numq.stash.websocket.SocketClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { SocketClient() } bind SocketApi::class
    single { FileService(get()) } bind FileApi::class
    single { FileData(get()) } bind FileRepository::class
    single { GetFiles(get()) }
    single { Refresh(get()) }
    single { SendFile(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
}