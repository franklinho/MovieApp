package com.example.testdemo.models

import com.example.testdemo.data.Movie
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel
class Movie(id : Int, title: String?, overview: String?, isAdult: Boolean, posterPath : String?, backDropPath: String?) {
    val id = 0
    val title: String? = null
    val overview: String? = null
    @SerializedName("adult")
    val isAdult = false
    @SerializedName("poster_path")
    val posterPath: String? = null
    @SerializedName("backdrop_path")
    val backdropPath: String? = null

    companion object {
        fun fromObject(movie : Movie) : com.example.testdemo.models.Movie {
            return com.example.testdemo.models.Movie(movie.id, movie.title, movie.overview, movie.isAdult, movie.posterPath, movie.backdropPath)
        }
    }
}