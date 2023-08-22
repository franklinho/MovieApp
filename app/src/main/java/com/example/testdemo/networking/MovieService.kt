package com.example.testdemo.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MovieService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
    val movieApi: MovieApi = retrofit.create(MovieApi::class.java)
    private val okHttpClient: OkHttpClient
        private get() {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxZmVjZGVhOWVjOTY1YWE3NDM3NzEzMzg4YmZkODQxOCIsInN1YiI6IjU5YjQyMTcwYzNhMzY4NGMzYTAwMWUwNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.f8wIQnErvwy01UTUenxKS-KYReg11H2gGRKGGQvVaUI")
                chain.proceed(request.build())
            }
            return builder.build()
        }

    companion object {
        private const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/original"
        fun getFullImageUrl(path: String): String {
            return BASE_IMAGE_URL + path
        }
    }
}