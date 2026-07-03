package com.example.testdemo.viewmodels

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Database
import com.example.testdemo.data.DatabaseBuilder
import com.example.testdemo.data.DatabaseHelperImpl
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import com.example.testdemo.recyclerview.InfiniteScrollListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.parceler.Parcels

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
            val tag = if (query == null) "MovieService/TrendingApi" else "MovieService/SearchAPI"

            try {
                val response = withContext(Dispatchers.IO) {
                    val call = if (query == null) movieApi.trendingMovies(1) else movieApi.searchMovies(query, 1)
                    call?.execute()
                }
                if (response != null && response.isSuccessful) {
                    //success
                    _uiState.value = MoviesUiState.Success(response.body()?.results ?: emptyList())
                } else {
                    //unsuccessful
                    _uiState.value = MoviesUiState.Error("Request unsuccessful")
                }
            } catch (t: Throwable) {
                //failed
                Log.d(tag, t.localizedMessage ?: "Request failed")
                _uiState.value = MoviesUiState.Error(t.localizedMessage ?: "Request failed")
            }
        }
    }

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        //TODO: Add shared element transition
        val action = MainFragmentDirections.actionMainFragmentToItemFragment()
            .setMovieTitle(movie.title)
            .setMoviePoster(movie.backdropPath)
            .setMovieOverview(movie.overview)
        fragment.requireView().findNavController().navigate(action)
    }

}
