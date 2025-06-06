package com.example.moviefan.ui.screens

import android.content.Intent
import android.widget.Toast
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviefan.data.entities.MovieEntity
import com.example.moviefan.viewmodel.ToWatchMoviesUiState
import com.example.moviefan.viewmodel.ToWatchMoviesViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.rememberCoroutineScope
import com.example.moviefan.data.repository.ToDoRepository
import com.example.moviefan.viewmodel.ToWatchMoviesViewModelFactory
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.moviefan.notification.NotificationScheduler
import kotlinx.coroutines.launch

@Composable
fun ToWatchMoviesScreen(
    repository: ToDoRepository
) {
    val viewModel: ToWatchMoviesViewModel = viewModel(
        factory = ToWatchMoviesViewModelFactory(repository) //ViewModel z przekazanym repozytorium
    )

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ToWatchMoviesUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White
                )
            }
        }
        is ToWatchMoviesUiState.Error -> {
            val error = uiState as ToWatchMoviesUiState.Error
            Text(
                text = stringResource(id = error.title) +" : "+error.message,
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )
        }
        is ToWatchMoviesUiState.Empty ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center){
                Text(
                    text = "No movies in database!",
                    color = Color.White,
                    fontSize = 24.sp
                    )
            }
        is ToWatchMoviesUiState.Success -> {
            val movies = (uiState as ToWatchMoviesUiState.Success).movies
                LazyColumn (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movies) { movie: MovieEntity ->
                        MovieDatabaseCard(
                            movie,
                            onDelete = {
                                movieToDelete -> viewModel.deleteMovie(movieToDelete)
                            }
                        )
                    }
                }
        }
    }
}

@Composable
fun MovieDatabaseCard(
    movie: MovieEntity,
    onDelete: (MovieEntity) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val notificationScheduler = NotificationScheduler(context)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF272626)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    notificationScheduler.cancelNotification(movie.id.toString())
                    coroutineScope.launch {
                        onDelete(movie)
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete movie",
                    tint = Color.Red
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(width = 150.dp, height = 250.dp)
                    ) {
                        MoviePosterLink(movie = movie)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box {
                        Column {
                            Text( //title
                                text = movie.title,
                                fontSize = 26.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text( //genre
                                text = "Genre: ${movie.genre}",
                                fontSize = 16.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text( //premiere-date
                                text = "RELEASE DATE: "+movie.releaseDate,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Box() {
                                Button(
                                    onClick = {
                                        notificationScheduler.scheduleNotification(movie.title, movie.releaseDate, movie.id.toString())
                                        coroutineScope.launch {
                                            Toast.makeText(context, "PUSH notification added!", Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                ) {
                                    Text(
                                        text = "PUSH"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoviePosterLink(movie: MovieEntity, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AsyncImage(
        model = movie.posterUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 150.dp, height = 250.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, movie.link?.toUri())
                context.startActivity(intent)
            }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun ToWatchMoviesScreenPreview() {
//    ToWatchMoviesScreen()
//}