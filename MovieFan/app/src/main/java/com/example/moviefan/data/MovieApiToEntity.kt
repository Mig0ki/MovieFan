package com.example.moviefan.data

import com.example.moviefan.api.models.Movie
import com.example.moviefan.data.entities.MovieEntity

//funkcje rozszerzające
fun Movie.toEntityWithMapGenres(genresMap: Map<Int, String>): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        releaseDate = releaseDate,
        genre = genreIds.mapNotNull { genresMap[it] }.joinToString(", "),
        posterUrl = posterUrl,
        link = link
    )
}

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        releaseDate = releaseDate,
        genre = genreIds.joinToString(","),
        posterUrl = posterUrl,
        link = link
    )
}