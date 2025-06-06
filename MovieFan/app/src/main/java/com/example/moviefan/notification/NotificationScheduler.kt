package com.example.moviefan.notification

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import android.util.Log
import java.util.Date

class NotificationScheduler(private val context: Context) {
    fun scheduleNotification(movieTitle: String, releaseDate: String, movieId: String) {
        val delay = calculateDelayUntilRelease(releaseDate)

        val scheduledTime = Date(System.currentTimeMillis() + delay)
        Log.d("NotificationScheduler", "PUSH notification for '$movieTitle' at: $scheduledTime")

        val data = workDataOf(
            "movieTitle" to movieTitle,
            "message" to "Release Day!"
        )

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag(movieId)
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWork)
    }

    fun cancelNotification(movieId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(movieId)
    }

    fun calculateDelayUntilRelease(releaseDateInput: String) :Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val releaseDate = LocalDate.parse(releaseDateInput, formatter).atStartOfDay()
        val now = LocalDateTime.now()

        val msRelease = releaseDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val msNow = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val delay = msRelease - msNow
        return if (delay > 0) delay else 0
    }
}