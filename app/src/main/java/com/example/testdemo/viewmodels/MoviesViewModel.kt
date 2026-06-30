package com.example.testdemo.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the trending/search movie list. */
sealed interface MoviesUiState {
    data object Loading : MoviesUiState
    data class Success(val movies: List<Movie>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}

/**
 * Slice 3: Hilt-injected, talking to [MovieRepository] (no direct networking).
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        requestTrendingMovies()
    }

    fun requestTrendingMovies() = loadMovies(query = null)

    fun searchMovies(query: String?) {
        if (query.isNullOrEmpty()) requestTrendingMovies() else loadMovies(query)
    }

    private fun loadMovies(query: String?) {
        _uiState.value = MoviesUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val movies = if (query == null) repository.trendingMovies(1) else repository.searchMovies(query, 1)
                MoviesUiState.Success(movies)
            } catch (t: Throwable) {
                MoviesUiState.Error(t.localizedMessage ?: "Request failed")
            }
        }
    }

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        val action = MainFragmentDirections.actionMainFragmentToItemFragment(
            movie.title, movie.backdropPath, movie.overview
        )
        fragment.requireView().findNavController().navigate(action)
    }
}
