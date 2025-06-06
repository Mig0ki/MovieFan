package com.example.moviefan.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.app.NotificationManager
import com.example.moviefan.R

fun showNotification(context: Context, title: String, message: String) {
    val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
    val builder = NotificationCompat.Builder(context, "movie_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager?.notify(1001, builder.build())
}