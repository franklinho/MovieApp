package com.example.testdemo.networking

import androidx.paging.PagingSource
import com.example.testdemo.models.Movie
import com.example.testdemo.models.MoviesResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPagingSourceTest {

    @Test
    fun `load returns first page with a next key`() = runTest {
        val api = mockk<MovieApi>()
        val movies = listOf(Movie(id = 1, title = "Batman"))
        coEvery { api.searchMovies("batman", 1) } returns
            MoviesResponse(page = 1, totalPages = 2, results = movies)

        val result = SearchPagingSource(api, "batman").load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(movies, result.data)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `load returns Error when the api throws`() = runTest {
        val api = mockk<MovieApi>()
        coEvery { api.searchMovies(any(), any()) } throws java.io.IOException("offline")

        val result = SearchPagingSource(api, "batman").load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
