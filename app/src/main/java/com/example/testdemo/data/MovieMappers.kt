package com.example.testdemo.data

import com.example.testdemo.models.Movie

fun Movie.toEntity(): MovieDto = MovieDto(
    id = id,
    title = title,
    overview = overview,
    isAdult = isAdult,
    posterPath = posterPath,
    backdropPath = backdropPath,
)

fun MovieDto.toModel(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    isAdult = isAdult,
    posterPath = posterPath,
    backdropPath = backdropPath,
)
