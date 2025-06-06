package com.example.moviefan.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefan.api.RetrofitClient
import com.example.moviefan.api.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DailyMovieUiState {
    object Choice : DailyMovieUiState
    data class Success(val movie: Movie) : DailyMovieUiState
    data class Error(@StringRes val title: Int, val message: String) : DailyMovieUiState
}

class DailyMovieViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DailyMovieUiState>(
        DailyMovieUiState.Choice
    )

    val uiState: StateFlow<DailyMovieUiState> = _uiState.asStateFlow()

    private val movieApi = RetrofitClient.apiServiceInstance

    fun backToChoice() {
        _uiState.value = DailyMovieUiState.Choice
    }

    private val _genresMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val genresMap: StateFlow<Map<Int, String>> = _genresMap

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = movieApi.getGenres()
                val map = response.genres.associate { it.id to it.name }
                _genresMap.value = map
            } catch (e: Exception) {
                Log.e("DailyMovieViewModel", "Błąd ładowania gatunków: ${e.message}")
            }
        }
    }

    fun getGenreIdByName(name: String): Int? {
        return _genresMap.value.entries.firstOrNull { it.value == name }?.key
    }

    fun getGenreNamesForMovie(genreIds: List<Int>): String {
        return genreIds.mapNotNull { _genresMap.value[it] }.joinToString(", ")
    }

    fun getRandomMovie(genreId : Int? = null) {

        viewModelScope.launch {
            try {
                val maxPage = 500
                val randomPage = (1..maxPage).random()

                val response = if (genreId != null) {
                    movieApi.getMoviesByGenre(genreId, randomPage)
                } else {
                    movieApi.getRandomMovies(randomPage)
                }

                val movies = response.results

                //zmiana stanów UI
                if (movies.isNotEmpty()) {
                    val randomMovie = movies.random()
                    Log.d("DailyMovieViewModel", "Losowy film: ${randomMovie.title}")
                    _uiState.value = DailyMovieUiState.Success(randomMovie)
                } else {
                    _uiState.value = DailyMovieUiState.Error(
                        title = android.R.string.dialog_alert_title,
                        message = "Brak filmów na stronie!"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = DailyMovieUiState.Error(
                    title = android.R.string.dialog_alert_title,
                    message = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    init {
        fetchGenres()
    }
}