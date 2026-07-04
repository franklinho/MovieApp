package com.example.testdemo.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.serialization.Serializable

@Serializable
object MovieListRoute

@Serializable
data class MovieDetailRoute(
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
                            title = movie.title,
                            backdropPath = movie.backdropPath,
                            overview = movie.overview,
                        ),
                    )
                },
            )
        }
        composable<MovieDetailRoute> { backStackEntry ->
            val detail = backStackEntry.toRoute<MovieDetailRoute>()
            MovieDetailScreen(
                title = detail.title,
                backdropPath = detail.backdropPath,
                overview = detail.overview,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
