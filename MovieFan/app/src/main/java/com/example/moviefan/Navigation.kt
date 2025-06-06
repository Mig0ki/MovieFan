package com.example.moviefan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviefan.ui.screens.DailyMovieScreen
import com.example.moviefan.ui.screens.IncomingMoviesScreen
import com.example.moviefan.ui.screens.ToWatchMoviesScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moviefan.data.repository.ToDoRepository

//destinations
enum class AppDestinations() {
    IncomingMovies,
    ToWatchMovies,
    DailyMovie
}

@Composable
fun Navigation(
    repository: ToDoRepository,
    navController: NavHostController = rememberNavController() //NavController - steruje nawigacją
) {
    //ustalanie na jakim ekranie aktualnie znajduje sie uzytkownik
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenName = backStackEntry?.destination?.route?.substringBefore("/")

    val currentScreen = AppDestinations.valueOf(
        screenName ?: AppDestinations.IncomingMovies.name
    )

    Scaffold(
        bottomBar = {
            val context = LocalContext.current
            val labels = context.resources.getStringArray(R.array.navigation)

            val items = listOf(
                AppDestinations.IncomingMovies,
                AppDestinations.ToWatchMovies,
                AppDestinations.DailyMovie
            )

            BottomAppBar(
                containerColor = Color.Black,
                modifier = Modifier.height(80.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    items.forEachIndexed { index, destination ->
                        val label = labels.getOrNull(index) ?: destination.name
                        val isActive = destination == currentScreen

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isActive) {
                                        navController.navigate(destination.name)
                                    }
                                }
                                .background(
                                    if (isActive)
                                        Color(0xFF272626)
                                    else
                                        Color.Transparent
                                )
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(4.dp)
                                    .background(Color.Yellow)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = label,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController=navController,
            startDestination = AppDestinations.IncomingMovies.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            composable(route = AppDestinations.IncomingMovies.name) {
                IncomingMoviesScreen(repository = repository)
            }
            composable(route = AppDestinations.ToWatchMovies.name) {
                ToWatchMoviesScreen(repository = repository)
            }
            composable(route = AppDestinations.DailyMovie.name) {
                DailyMovieScreen()
            }
        }
    }
}