package com.numq.stash.notification

interface NotificationApi {
    fun showDownloadNotification(uri: String, type: String = "*/*")
}