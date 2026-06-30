package com.example.testdemo.data

import com.example.testdemo.models.Movie as MovieDto

fun MovieDto.toEntity(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    isAdult = isAdult,
    posterPath = posterPath,
    backdropPath = backdropPath,
)

fun Movie.toDto(): MovieDto = MovieDto(
    id = id,
    title = title,
    overview = overview,
    isAdult = isAdult,
    posterPath = posterPath,
    backdropPath = backdropPath,
)
