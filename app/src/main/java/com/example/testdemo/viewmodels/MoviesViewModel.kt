package com.example.testdemo.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Slice 5: exposes a single `Flow<PagingData<Movie>>` that switches between the trending
 * (Room-backed, RemoteMediator) and search (network) pagers via [flatMapLatest] on the query.
 * Paging's LoadState supersedes the old MoviesUiState.
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

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        val action = MainFragmentDirections.actionMainFragmentToItemFragment(
            movie.title, movie.backdropPath, movie.overview
        )
        fragment.requireView().findNavController().navigate(action)
    }
}
