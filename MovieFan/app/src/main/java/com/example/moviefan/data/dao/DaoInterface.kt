package com.example.moviefan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviefan.data.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

//interfejs DAO - zawiera metody do wykonywania operacji na bazie
@Dao
interface DaoInterface {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    //ciągłe obserwowanie
    @Query("SELECT * FROM movies")
    fun getAllMoviesFlow(): Flow<List<MovieEntity>>

    //jednorazowe pobranie danych
    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies ORDER BY releaseDate ASC")
    suspend fun getAllMoviesSortedByDate(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Int): Flow<MovieEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :id)")
    suspend fun isMovieSaved(id: Int): Boolean
}