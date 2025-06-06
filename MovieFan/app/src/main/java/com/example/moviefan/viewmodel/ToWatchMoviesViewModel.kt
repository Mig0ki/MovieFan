package com.example.moviefan.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviefan.data.entities.MovieEntity
import com.example.moviefan.data.repository.ToDoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

sealed interface ToWatchMoviesUiState {
    object Loading : ToWatchMoviesUiState
    object Empty : ToWatchMoviesUiState
    data class Success(val movies: List<MovieEntity>) : ToWatchMoviesUiState
    data class Error(@StringRes val title: Int, val message: String) : ToWatchMoviesUiState
}

class ToWatchMoviesViewModel (
    private val repository: ToDoRepository
) : ViewModel( ) {

    //przechowywanie stanów
    private val _uiState = MutableStateFlow<ToWatchMoviesUiState>(
        ToWatchMoviesUiState.Loading
    )
    val uiState: StateFlow<ToWatchMoviesUiState> =  _uiState.asStateFlow()

    //przechowywanie zapisanych filmów
    private val _movies = mutableStateOf<List<MovieEntity>>(emptyList())
    val movies: State<List<MovieEntity>> = _movies

    //załadowanie filmów
    private fun loadMovies() {
        viewModelScope.launch {
            val movies = repository.getAllMoviesSortedByDate()
        }
    }

    //usunięcie filmów
    fun deleteMovie(movie: MovieEntity) {
        viewModelScope.launch {
            repository.deleteMovie(movie)

            val updatedMovies = repository.getAllMoviesSortedByDate()

            if (updatedMovies.isEmpty()) {
                _uiState.value = ToWatchMoviesUiState.Empty
            } else {
                _uiState.value = ToWatchMoviesUiState.Success(updatedMovies)
            }

        }
    }

    //inicjalizacja
    init {
        loadMovies()
        viewModelScope.launch {
            try {
                val movies = repository.getAllMoviesSortedByDate()
                if (movies.isEmpty()) {
                    _uiState.value = ToWatchMoviesUiState.Empty
                } else {
                    _uiState.value = ToWatchMoviesUiState.Success(movies)
                }
            } catch (e: Exception) {
                _uiState.value = ToWatchMoviesUiState.Error(
                    title = android.R.string.dialog_alert_title,
                    message = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }
}

//klasa umożliwiająca na utworzenie ToWatchMoviesViewModel z przekazanym repozytorium
class ToWatchMoviesViewModelFactory(
    private val repository: ToDoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToWatchMoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToWatchMoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}