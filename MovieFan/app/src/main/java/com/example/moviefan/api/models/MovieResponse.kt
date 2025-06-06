package com.example.moviefan.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//model danych - do pobierania danych dotyczących filmów (MovieResponse i Movie)

@JsonClass(generateAdapter = true)
data class MovieResponse(
    @Json(name = "results") val results: List<Movie>
)

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "genre_ids") val genreIds: List<Int>,
    @Json(name = "id") val id: Int,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "title") val title: String,
) {
    val link: String
        //definicja gettera do wydobycia linku
        get() = "https://www.themoviedb.org/movie/$id"
    val posterUrl: String?
        //definicja gettera do zdobycia ścieżki
        get() = posterPath?.let {"https://image.tmdb.org/t/p/w500$it"}
}