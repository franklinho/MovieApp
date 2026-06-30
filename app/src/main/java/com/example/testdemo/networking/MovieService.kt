package com.example.testdemo.networking

import com.example.testdemo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class MovieService {
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val movieApi: MovieApi = retrofit.create(MovieApi::class.java)

    private val okHttpClient: OkHttpClient
        get() {
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
        fun getFullImageUrl(path: String): String = BASE_IMAGE_URL + path
    }
}
