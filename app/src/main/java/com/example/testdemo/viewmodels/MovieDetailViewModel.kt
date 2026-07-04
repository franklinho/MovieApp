package com.example.testdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import com.example.testdemo.ui.MovieDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MovieDetailRoute>()

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                repository.movie(route.movieId)
            }.onSuccess { movie ->
                _uiState.value = MovieDetailUiState(
                    movie = movie,
                    isLoading = false,
                    errorMessage = if (movie == null) "Movie not found" else null,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (it.movie == null) {
                            error.localizedMessage ?: "Something went wrong"
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }
}
