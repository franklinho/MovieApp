package com.example.testdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.testdemo.models.Movie
import com.example.testdemo.viewmodels.MovieDetailViewModel
import com.example.testdemo.viewmodels.MovieDetailUiState
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.serialization.Serializable

@Serializable
object MovieListRoute

@Serializable
data class MovieDetailRoute(
    val movieId: Int,
)

@Composable
fun MovieAppNavHost(navController: NavHostController = rememberNavController()) {
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    NavHost(
        navController = navController,
        startDestination = MovieListRoute,
    ) {
        composable<MovieListRoute> {
            MovieListScreen(
                viewModel = hiltViewModel<MoviesViewModel>(),
                onMovieClick = { movie ->
                    selectedMovie = movie
                    navController.navigate(
                        MovieDetailRoute(movieId = movie.id),
                    )
                },
            )
        }
        composable<MovieDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MovieDetailRoute>()
            val viewModel = hiltViewModel<MovieDetailViewModel>()
            val uiState by viewModel.uiState.collectAsState()
            val fallbackMovie = selectedMovie?.takeIf { it.id == route.movieId }
            MovieDetailScreen(
                uiState = uiState.withFallback(fallbackMovie),
                onBack = { navController.popBackStack() },
                onRetry = viewModel::retry,
            )
        }
    }
}

private fun MovieDetailUiState.withFallback(fallbackMovie: Movie?): MovieDetailUiState =
    if (movie != null || fallbackMovie == null) {
        this
    } else {
        copy(
            movie = fallbackMovie,
            isLoading = false,
            errorMessage = null,
        )
    }
