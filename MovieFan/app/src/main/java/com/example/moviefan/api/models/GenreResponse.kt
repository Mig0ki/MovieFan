package com.example.moviefan.api.models

import com.squareup.moshi.JsonClass

//model danych - do pobierania listy gatunków

@JsonClass(generateAdapter = true)
data class GenreResponse(
    val genres: List<Genre>
)

@JsonClass(generateAdapter = true)
data class Genre(
    val id: Int,
    val name: String
)