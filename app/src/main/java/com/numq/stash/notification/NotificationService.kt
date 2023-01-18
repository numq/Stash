package com.numq.stash.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.numq.stash.R


class NotificationService constructor(
    private val context: Context
) {
    companion object {
        const val DOWNLOAD_CHANNEL_ID = "101"
        const val DOWNLOAD_NOTIFICATION_ID = 0
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    fun showDownloadNotification(uri: String, type: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelCompat.Builder(
                DOWNLOAD_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            ).build()
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
            setTimeoutAfter(5 * 1000L)
        }.build()
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, notification)
    }
}