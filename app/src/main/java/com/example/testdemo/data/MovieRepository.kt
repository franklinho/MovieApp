package com.example.testdemo.data

import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Slice 4: Room is the source of truth for trending display. [trendingMovies] streams the
 * cache; [refreshTrending] pulls from the network and writes through (the DAO Flow then
 * re-emits). Offline with a populated cache still shows movies. Search stays network-direct
 * for now (it becomes a separate paged source in Slice 5).
 */
@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDao: MovieDao,
) {
    fun trendingMovies(): Flow<List<Movie>> =
        movieDao.observeAll().map { entities -> entities.map { it.toDto() } }

    suspend fun refreshTrending() {
        val fresh = movieApi.trendingMovies(1).results ?: emptyList()
        movieDao.replaceAll(fresh.map { it.toEntity() })
    }

    suspend fun searchMovies(query: String, page: Int): List<Movie> =
        movieApi.searchMovies(query, page).results ?: emptyList()
}
