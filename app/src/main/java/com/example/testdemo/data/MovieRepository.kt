package com.example.testdemo.data

import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single seam between the ViewModel and data sources. Slice 3 wraps the network only;
 * Slice 4 adds the Room cache (network-with-cache-fallback) behind this same API.
 */
@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
) {
    suspend fun trendingMovies(page: Int): List<Movie> =
        movieApi.trendingMovies(page).results ?: emptyList()

    suspend fun searchMovies(query: String, page: Int): List<Movie> =
        movieApi.searchMovies(query, page).results ?: emptyList()
}
