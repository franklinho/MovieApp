package com.example.testdemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Exposes a `Flow<PagingData<Movie>>` that switches between the trending Room-backed pager
 * and the network-only search pager.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val query = MutableStateFlow<String?>(null)

    val movies: Flow<PagingData<Movie>> = query
        .flatMapLatest { q ->
            if (q.isNullOrEmpty()) repository.trendingPager() else repository.searchPager(q)
        }
        .cachedIn(viewModelScope)

    fun requestTrendingMovies() {
        query.value = null
    }

    fun searchMovies(query: String?) {
        this.query.value = query
    }

    class Factory(
        private val repository: MovieRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
                return MoviesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
