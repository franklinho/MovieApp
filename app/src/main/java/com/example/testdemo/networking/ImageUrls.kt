package com.example.testdemo.networking

object ImageUrls {
    private const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/original"
    fun fullImageUrl(path: String): String = BASE_IMAGE_URL + path
}
