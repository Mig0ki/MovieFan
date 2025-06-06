package com.example.moviefan.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.moviefan.R
import com.example.moviefan.api.RetrofitClient
import com.example.moviefan.api.models.Movie
import com.example.moviefan.api.models.MovieResponse
import com.example.moviefan.data.entities.MovieEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewModelScope
import com.example.moviefan.data.repository.ToDoRepository
import kotlinx.coroutines.launch

sealed interface IncomingMoviesScreenUiState {
    object Loading : IncomingMoviesScreenUiState
    data class Success(val categories: List<Movie>) : IncomingMoviesScreenUiState
    data class Error(@StringRes val title: Int, val message: String) : IncomingMoviesScreenUiState
}

class IncomingMoviesViewModel : ViewModel() {
    private val _savedMovieIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedMovieIds: StateFlow<Set<Int>> = _savedMovieIds.asStateFlow()

    //pobieranie filmów z repozytorium i dodanie ich ID do _savedMovieIds
    fun loadAddedMovies(repository: ToDoRepository) {
        viewModelScope.launch {
            val movies = repository.getAllMovies()
            _savedMovieIds.value = movies.map { it.id }.toSet()
        }
    }

    //dodanie do bazy danych
    fun addMovieToRepo(movieEntity: MovieEntity, repository: ToDoRepository) {
        viewModelScope.launch {
            repository.addMovie(movieEntity)
            loadAddedMovies(repository) //odświeżenie listy zapisanych filmów
        }
    }

    private val _uiState = MutableStateFlow<IncomingMoviesScreenUiState>(
        IncomingMoviesScreenUiState.Loading
    )
    val uiState: StateFlow<IncomingMoviesScreenUiState> =  _uiState.asStateFlow()

    //instancja API
    private val movieApi = RetrofitClient.apiServiceInstance

    private fun fetchMovies() {
        _uiState.value = IncomingMoviesScreenUiState.Loading

        //formatowanie dat i przystosowanie do zapytania
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val now = LocalDate.now()
        val oneMonthLater = now.plusMonths(1)

        val fromDate = now.format(formatter)
        val toDate = oneMonthLater.format(formatter)

        //wywołanie zapytania do API
        movieApi.getUpcomingMovies(fromDate, toDate).enqueue(upcomingMoviesCallback)
    }

    //zarządzanie wywołaniem zapytania
    private val upcomingMoviesCallback = object : Callback<MovieResponse> {
        override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
            if (response.isSuccessful) {
                val movies = response.body()?.results ?: emptyList()
                Log.d("ViewModel", "Movies received: $movies")
                _uiState.value = IncomingMoviesScreenUiState.Success(movies)
            } else {
                Log.e("ViewModel", "API error: ${response.message()}")
                _uiState.value = IncomingMoviesScreenUiState.Error(
                    title = R.string.error,
                    message = "API error: ${response.message()}"
                )
            }
        }
        override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
            Log.e("ViewModel", "Network failure: ${t.message}")
            _uiState.value = IncomingMoviesScreenUiState.Error(
                title = R.string.network_error,
                message = "No connection: ${t.message}"
            )
        }
    }

    //obsluga gatunkow (mapowanie id->nazwa)
    private val _genresMap = mutableMapOf<Int, String>()

    fun getGenresMap(): Map<Int, String> = _genresMap.toMap()

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = movieApi.getGenres()
                val genres = response.genres

                _genresMap.clear()
                genres.forEach {
                    _genresMap[it.id] = it.name
                }

                Log.d("ViewModel", "Genres loaded: $_genresMap")

            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to load genres: ${e.message}")
                _uiState.value = IncomingMoviesScreenUiState.Error(
                    title = R.string.network_error,
                    message = "API error loading genres: ${e.message}"
                )
            }
        }
    }

    fun getGenreNamesForMovie(genreIds: List<Int>): String {
        return genreIds.mapNotNull { _genresMap[it] }.joinToString(", ")
    }

    init {
        fetchMovies()
        fetchGenres()
    }
}