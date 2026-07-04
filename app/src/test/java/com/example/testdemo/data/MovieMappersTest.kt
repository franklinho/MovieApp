package com.example.testdemo.data

import com.example.testdemo.models.Movie
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `model maps to entity with given orderIndex`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            overview = "A thief who steals corporate secrets.",
            isAdult = false,
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
        )

        val entity = movie.toDto(orderIndex = 7)

        assertEquals(1, entity.id)
        assertEquals("Inception", entity.title)
        assertEquals("/poster.jpg", entity.posterPath)
        assertEquals(7, entity.orderIndex)
    }

    @Test
    fun `entity round-trips back to an equal model`() {
        val movie = Movie(
            id = 42,
            title = "Interstellar",
            overview = "Explorers travel through a wormhole.",
            isAdult = false,
            posterPath = "/p.jpg",
            backdropPath = "/b.jpg",
        )

        assertEquals(movie, movie.toDto(orderIndex = 0).toModel())
    }
}
