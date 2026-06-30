package com.example.testdemo.data

import com.example.testdemo.models.Movie as MovieDto
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `dto maps to entity with given orderIndex`() {
        val dto = MovieDto(
            id = 1,
            title = "Inception",
            overview = "A thief who steals corporate secrets.",
            isAdult = false,
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
        )

        val entity = dto.toEntity(orderIndex = 7)

        assertEquals(1, entity.id)
        assertEquals("Inception", entity.title)
        assertEquals("/poster.jpg", entity.posterPath)
        assertEquals(7, entity.orderIndex)
    }

    @Test
    fun `entity round-trips back to an equal dto`() {
        val dto = MovieDto(
            id = 42,
            title = "Interstellar",
            overview = "Explorers travel through a wormhole.",
            isAdult = false,
            posterPath = "/p.jpg",
            backdropPath = "/b.jpg",
        )

        assertEquals(dto, dto.toEntity(orderIndex = 0).toDto())
    }
}
