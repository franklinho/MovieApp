package com.example.testdemo.data

import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDao: MovieDao,
) {
    fun trendingMovies(): Flow<List<Movie>> =
        movieDao.observeAll().map { entities -> entities.map { it.toModel() } }

    suspend fun refreshTrending() {
        val fresh = movieApi.trendingMovies(1).results ?: emptyList()
        movieDao.replaceAll(fresh.map { it.toEntity() })
    }

    suspend fun searchMovies(query: String, page: Int): List<Movie> =
        movieApi.searchMovies(query, page).results ?: emptyList()
}
