package com.example.testdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testdemo.viewmodels.MovieDetailViewModel
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.serialization.Serializable

@Serializable
object MovieListRoute

@Serializable
data class MovieDetailRoute(
    val movieId: Int,
    val title: String?,
    val backdropPath: String?,
    val overview: String?,
)

@Composable
fun MovieAppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = MovieListRoute,
    ) {
        composable<MovieListRoute> {
            MovieListScreen(
                viewModel = hiltViewModel<MoviesViewModel>(),
                onMovieClick = { movie ->
                    navController.navigate(
                        MovieDetailRoute(
                            movieId = movie.id,
                            title = movie.title,
                            backdropPath = movie.backdropPath,
                            overview = movie.overview,
                        ),
                    )
                },
            )
        }
        composable<MovieDetailRoute> {
            val viewModel = hiltViewModel<MovieDetailViewModel>()
            val uiState by viewModel.uiState.collectAsState()
            MovieDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
