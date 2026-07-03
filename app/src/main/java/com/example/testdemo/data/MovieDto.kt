package com.example.testdemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieDto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "adult") val isAdult: Boolean,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?
)
