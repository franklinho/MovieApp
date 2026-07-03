package com.example.testdemo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    val page: Int = 0,
    @SerialName("total_pages") val totalPages: Int = 0,
    val results: List<Movie>? = null,
)
