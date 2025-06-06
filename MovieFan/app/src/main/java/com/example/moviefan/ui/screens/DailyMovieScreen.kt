package com.example.moviefan.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.moviefan.api.models.Movie
import com.example.moviefan.viewmodel.DailyMovieUiState
import com.example.moviefan.viewmodel.DailyMovieViewModel

@Composable
fun DailyMovieScreen(viewModel: DailyMovieViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    val genres by viewModel.genresMap.collectAsState()
    val options = genres.values.toList()
    var selectedGenreId by remember { mutableStateOf<Int?>(null) }
    var selectedOption by remember { mutableStateOf("Genre") }
    var isFabVisible by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is DailyMovieUiState.Choice -> {
                Button(
                    onClick = {
                        val genreId = viewModel.getGenreIdByName(selectedOption)
                        viewModel.getRandomMovie(genreId)
                        isFabVisible = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .size(width = 200.dp, height = 160.dp)
                ) {
                    Text(
                        text = "Generate Daily Movie",
                        color = Color.Black,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box {
                    Button(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF272626)),
                        modifier = Modifier
                            .size(width = 200.dp, height = 50.dp)
                    ) {
                        Text(
                            text = selectedOption,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown-Icon",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(200.dp)
                            .height(180.dp)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOption = option
                                    selectedGenreId = genres.entries.firstOrNull { it.value == option }?.key
                                    expanded = false
                                },
                                text = { Text(option) }
                            )
                        }
                    }
                }
            }

            is DailyMovieUiState.Error -> {
                val error = uiState as DailyMovieUiState.Error
                Text(
                    text = stringResource(id = error.title) +" : "+error.message,
                    color = Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
            }

            is DailyMovieUiState.Success -> {
                val movie = (uiState as DailyMovieUiState.Success).movie
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    DailyMovieCard(movie, viewModel)
                    if (isFabVisible) {
                        FloatingActionButton(
                            onClick = {
                                isFabVisible = false
                                viewModel.backToChoice()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyMovieCard(
    movie: Movie,
    viewModel: DailyMovieViewModel
    ) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DailyMoviePoster(movie)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text( // Title
                    text = movie.title,
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text( // Genre
                    text = "Genre: ${viewModel.getGenreNamesForMovie(movie.genreIds)}",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text( // Release Date
                    text = "Release Date: ${movie.releaseDate}",
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                val context = LocalContext.current
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, movie.link.toUri())
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
                ) {
                    Text(
                        text = "MORE INFORMATION",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DailyMoviePoster(movie: Movie, modifier: Modifier = Modifier) {
    AsyncImage(
        model = movie.posterUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 150.dp, height = 250.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}