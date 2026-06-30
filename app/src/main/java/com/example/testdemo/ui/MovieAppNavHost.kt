package com.example.testdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testdemo.viewmodels.MovieDetailViewModel
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.serialization.Serializable

/** Type-safe Navigation Compose routes (Navigation 2.8+ + kotlinx.serialization). */
@Serializable
object MovieListRoute

@Serializable
data class MovieDetailRoute(
    val movieId: Int,
    val title: String? = null,
    val backdropPath: String? = null,
    val overview: String? = null,
)

@Composable
fun MovieAppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = MovieListRoute) {
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
                        )
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
                onRetry = viewModel::retry,
            )
        }
    }
}
