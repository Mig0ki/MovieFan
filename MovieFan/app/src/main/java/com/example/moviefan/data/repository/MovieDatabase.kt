package com.example.moviefan.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviefan.data.dao.DaoInterface
import com.example.moviefan.data.entities.MovieEntity

//klasa abstrakcyjna
@Database(entities = [MovieEntity::class], version = 1)
abstract class MovieDatabase() : RoomDatabase() {
    abstract fun dao() : DaoInterface

    companion object {
        private const val DATABASE_NAME = "movies_database"

        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration() //usuń bazę jeśli wykryjesz niezgodność wersji
                    .build().also {INSTANCE = it}
            }
        }
    }
}