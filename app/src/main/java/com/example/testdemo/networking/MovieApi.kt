package com.example.testdemo.networking

import com.example.testdemo.models.MoviesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    //TODO: Add api parameters to support pagination
    @get:GET("3/trending/movie/week")
    val trendingMovies: Call<MoviesResponse?>?

    @GET("3/search/movie")
    fun searchMovies(@Query("query") query: String?): Call<MoviesResponse?>?
}
