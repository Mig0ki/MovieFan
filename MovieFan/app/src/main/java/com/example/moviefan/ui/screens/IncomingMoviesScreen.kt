package com.example.moviefan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviefan.viewmodel.IncomingMoviesScreenUiState
import com.example.moviefan.viewmodel.IncomingMoviesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moviefan.api.models.Movie
import com.example.moviefan.data.entities.MovieEntity
import com.example.moviefan.data.repository.ToDoRepository
import com.example.moviefan.data.toEntity
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import com.example.moviefan.data.toEntityWithMapGenres

@Composable
fun IncomingMoviesScreen(
    repository: ToDoRepository,
    viewModel: IncomingMoviesViewModel = viewModel()
) {
    //pobranie aktualnego stanu z ViewModel
    val uiState by viewModel.uiState.collectAsState()

    when(uiState) {
        is IncomingMoviesScreenUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is IncomingMoviesScreenUiState.Success -> {
            val movies = (uiState as IncomingMoviesScreenUiState.Success).categories
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies) { movie ->
                        MovieIncomingCard(movie = movie, viewModel = viewModel, repository = repository)
                    }
                }
            }
        }
        is IncomingMoviesScreenUiState.Error -> {
            val error = uiState as IncomingMoviesScreenUiState.Error
            Text(
                text = stringResource(id = error.title)+" : "+error.message,
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}


@Composable
fun MovieIncomingCard(
    movie: Movie,
    viewModel: IncomingMoviesViewModel,
    repository: ToDoRepository,
) {
    LaunchedEffect(Unit) {
        viewModel.loadAddedMovies(repository)
    }

    val coroutineScope = rememberCoroutineScope() //kurutyna powiązana z composable

    val savedMovieIds by viewModel.savedMovieIds.collectAsState() //pobieranie listy zapisanych filmów
    val isSaved = savedMovieIds.contains(movie.id) //sprawdzenie czy dany film już został zapisany

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoviePoster(movie = movie.toEntity())

                Spacer(modifier = Modifier.width(12.dp))

                Box {
                    Column {
                        Text( //tytuł
                            text = movie.title,
                            fontSize = 26.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text( //getunek
                            text = "Genre: ${viewModel.getGenreNamesForMovie(movie.genreIds)}",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text( //data premiery
                            text = "Release Date: "+movie.releaseDate,
                            fontSize = 16.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch{
                                    val movieEntity = movie.toEntityWithMapGenres(viewModel.getGenresMap())
                                    viewModel.addMovieToRepo(movieEntity, repository)
                                }
                            },
                            colors = if (isSaved) ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            else ButtonDefaults.buttonColors(containerColor = Color.White),
                            enabled = !isSaved
                        ) {
                            Text(
                                text = if (isSaved) "ADDED" else "ADD TO LIST",
                                color = if (isSaved) Color.Gray else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoviePoster(movie: MovieEntity, modifier: Modifier = Modifier) {
    AsyncImage(
        model = movie.posterUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 150.dp, height = 250.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}