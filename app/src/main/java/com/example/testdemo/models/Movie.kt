package com.example.testdemo.models

import com.example.testdemo.data.Movie
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id : Int = 0,
    val title: String? = null,
    val overview: String? = null,

    @SerialName("adult") val isAdult: Boolean = false,
    @SerialName("poster_path") val posterPath : String? = null,
    @SerialName("backdrop_path") val backDropPath: String? = null
)
