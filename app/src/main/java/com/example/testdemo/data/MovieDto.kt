package com.example.testdemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room cache entity. [orderIndex] preserves the trending rank order across paged inserts
 * (Room's PagingSource needs a stable ORDER BY). Mapped to/from the API model in MovieMappers.kt.
 */
@Entity(tableName = "Movie")
data class MovieDto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "adult") val isAdult: Boolean,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
)
