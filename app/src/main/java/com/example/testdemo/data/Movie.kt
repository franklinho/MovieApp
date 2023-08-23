package com.example.testdemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testdemo.models.Movie

@Entity
data class Movie(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "adult") val isAdult: Boolean,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?
) {
    companion object {
        fun fromObject(movie : Movie) : com.example.testdemo.data.Movie {
            return com.example.testdemo.data.Movie(movie.id, movie.title, movie.overview, movie.isAdult, movie.posterPath, movie.backdropPath)
        }
    }
}