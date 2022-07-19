package com.numq.stash.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.numq.stash.R

class NotificationService constructor(private val context: Context) : NotificationApi {

    companion object {
        const val DOWNLOAD_CHANNEL_ID = "101"
        const val DOWNLOAD_CHANNEL_NAME = "download"
        const val DOWNLOAD_NOTIFICATION_ID = 1
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        id: String,
        name: String,
        importance: Int
    ) {
        val channel = NotificationChannel(id, name, importance).apply {
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun showDownloadNotification(uri: String, type: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
        }
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(Uri.parse(uri), type)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID).apply {
            setContentTitle("File downloaded")
            setContentText("Click to open")
            setSmallIcon(R.drawable.file_download_done)
            setFullScreenIntent(pendingIntent, true)
            setTimeoutAfter(3 * 1000L)
        }.build()
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, notification)
    }
}