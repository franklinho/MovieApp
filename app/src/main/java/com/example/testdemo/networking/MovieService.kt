package com.example.testdemo.networking

import com.example.testdemo.BuildConfig
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
                    .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_TOKEN}")
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