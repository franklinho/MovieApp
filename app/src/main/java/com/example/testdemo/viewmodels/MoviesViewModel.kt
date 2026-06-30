package com.example.testdemo.viewmodels

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** UI state for the trending/search movie list. */
sealed interface MoviesUiState {
    data object Loading : MoviesUiState
    data class Success(val movies: List<Movie>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}

/**
 * Slice 1: clean ViewModel with a single observable [uiState].
 *
 * The [movieApi] parameter is a constructor seam (default-valued so the no-arg
 * `by viewModels()` factory still works via @JvmOverloads) — Slice 2 makes the API
 * `suspend` and adds the first unit test; Slice 3 swaps this for Hilt injection.
 * Pagination is intentionally dropped here (it was already non-functional) and
 * returns in Slice 5 via Paging 3 + RemoteMediator.
 */
class MoviesViewModel @JvmOverloads constructor(
    private val movieApi: MovieApi = MovieService().movieApi
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
            val tag = if (query == null) "MovieService/TrendingApi" else "MovieService/SearchAPI"
            try {
                val response = withContext(Dispatchers.IO) {
                    val call = if (query == null) movieApi.trendingMovies(1) else movieApi.searchMovies(query, 1)
                    call?.execute()
                }
                if (response != null && response.isSuccessful) {
                    _uiState.value = MoviesUiState.Success(response.body()?.results ?: emptyList())
                } else {
                    _uiState.value = MoviesUiState.Error("Request unsuccessful")
                }
            } catch (t: Throwable) {
                Log.d(tag, t.localizedMessage ?: "Request failed")
                _uiState.value = MoviesUiState.Error(t.localizedMessage ?: "Request failed")
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
