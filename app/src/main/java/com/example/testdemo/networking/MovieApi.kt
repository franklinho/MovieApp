package com.example.testdemo.networking

import com.example.testdemo.models.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("3/trending/movie/week")
    suspend fun trendingMovies(@Query("page") page: Int): MoviesResponse

    @GET("3/search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int): MoviesResponse
}
