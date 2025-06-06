package com.example.moviefan

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moviefan.data.repository.MovieDatabase
import com.example.moviefan.data.repository.ToDoRepository

class MovieFanApplication: Application() {
    lateinit var toDoRepository: ToDoRepository
        private set

    override fun onCreate() {
        super.onCreate()

        toDoRepository = ToDoRepository(MovieDatabase.getDatabase(this).dao())
        createNotificationChannel()
    }

    //powiadmienia PUSH - kanał
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "movie_channel", // ID kanału
            "Daily Movie Notifications", // nazwa kanału
            NotificationManager.IMPORTANCE_DEFAULT // ważność kanału
        ).apply {
            description = "Notifications about daily movie recommendations"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

}