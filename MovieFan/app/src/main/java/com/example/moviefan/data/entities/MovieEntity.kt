package com.example.moviefan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//reprezentacja tabeli w bazie danych
@Entity(tableName = "movies")
data class MovieEntity(
    var title: String,
    var posterUrl: String?,
    var link: String?,
    var releaseDate: String,
    var genre: String,
    @PrimaryKey
    val id: Int
)