package com.example.testdemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Slice 7: pure data surface — exposes a `Flow<PagingData<Movie>>` that switches between the
 * trending (Room-backed) and search (network) pagers. Navigation now lives in Compose
 * (MovieAppNavHost), so the old `launchMovieFragment` is gone.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoviesViewModel @Inject constructor(
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
}
