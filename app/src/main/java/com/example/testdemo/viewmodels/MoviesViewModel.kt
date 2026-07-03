package com.example.testdemo.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MoviesUiState {
    data object Loading : MoviesUiState
    data class Success(val movies: List<Movie>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        requestTrendingMovies()
    }

    fun requestTrendingMovies() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            launch {
                try {
                    repository.refreshTrending()
                } catch (t: Throwable) {
                    if (_uiState.value !is MoviesUiState.Success) {
                        _uiState.value = MoviesUiState.Error(t.localizedMessage ?: "Request failed")
                    }
                }
            }
            repository.trendingMovies().collect { movies ->
                if (movies.isNotEmpty()) {
                    _uiState.value = MoviesUiState.Success(movies)
                }
            }
        }
    }

    fun searchMovies(query: String?) {
        if (query.isNullOrEmpty()) {
            requestTrendingMovies()
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            _uiState.value = try {
                MoviesUiState.Success(repository.searchMovies(query, 1))
            } catch (t: Throwable) {
                MoviesUiState.Error(t.localizedMessage ?: "Request failed")
            }
        }
    }

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        val action = MainFragmentDirections.actionMainFragmentToItemFragment()
            .setMovieTitle(movie.title)
            .setMoviePoster(movie.backdropPath)
            .setMovieOverview(movie.overview)
        fragment.requireView().findNavController().navigate(action)
    }
}
