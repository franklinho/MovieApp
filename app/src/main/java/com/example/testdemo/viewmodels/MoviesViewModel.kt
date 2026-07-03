package com.example.testdemo.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MoviesUiState {
    data object Loading : MoviesUiState
    data class Success(val movies: List<Movie>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}

class MoviesViewModel @JvmOverloads constructor(private val movieApi: MovieApi = MovieService().movieApi) :
    ViewModel() {
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        requestTrendingMovies()
    }

    fun requestTrendingMovies() {
        loadMovies(query = null)
    }

    fun searchMovies(query: String?) {
        if (query.isNullOrEmpty()) requestTrendingMovies() else loadMovies(query)
    }

    private fun loadMovies(query: String?) {
        _uiState.value = MoviesUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val response = if (query == null) movieApi.trendingMovies(1) else movieApi.searchMovies(query, 1)
                MoviesUiState.Success(response.results ?: emptyList())
            } catch (t: Throwable) {
                MoviesUiState.Error(t.localizedMessage ?: "Request failed")
            }
        }
    }

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        //TODO: Add shared element transition
        val action = MainFragmentDirections.actionMainFragmentToItemFragment()
            .setMovieTitle(movie.title)
            .setMoviePoster(movie.backDropPath)
            .setMovieOverview(movie.overview)
        fragment.requireView().findNavController().navigate(action)
    }

}
