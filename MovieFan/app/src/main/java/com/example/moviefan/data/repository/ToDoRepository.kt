package com.example.moviefan.data.repository

import com.example.moviefan.data.dao.DaoInterface
import com.example.moviefan.data.entities.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//repozytorium
class ToDoRepository(private val dao: DaoInterface) {
    fun getMovieById(id: Int) = dao.getMovieById(id)

    suspend fun addMovie(movie: MovieEntity) {
        withContext(Dispatchers.IO) {
            dao.insertMovie(movie)
        }
    }

    suspend fun deleteMovie(movie: MovieEntity) {
        withContext(Dispatchers.IO) {
            dao.deleteMovie(movie)
        }
    }

    suspend fun isMovieInDatabase(id: Int): Boolean {
        return dao.isMovieSaved(id)
    }

    suspend fun getAllMovies(): List<MovieEntity> {
        return dao.getAllMovies()
    }

    suspend fun getAllMoviesSortedByDate(): List<MovieEntity> {
        return dao.getAllMoviesSortedByDate()
    }
}