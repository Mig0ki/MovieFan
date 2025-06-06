package com.example.moviefan.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker (
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val movieTitle = inputData.getString("movieTitle") ?: "Movie"
        val message = inputData.getString("message") ?: "Premiere today!"

        showNotification(applicationContext, movieTitle, message)
        return Result.success()
    }
}