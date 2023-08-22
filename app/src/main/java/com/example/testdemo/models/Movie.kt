package com.example.testdemo.models

import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel
class Movie {
    val id = 0
    val title: String? = null
    val overview: String? = null
    @SerializedName("adult")
    val isAdult = false
    @SerializedName("poster_path")
    val posterPath: String? = null
    @SerializedName("backdrop_path")
    val backdropPath: String? = null
}