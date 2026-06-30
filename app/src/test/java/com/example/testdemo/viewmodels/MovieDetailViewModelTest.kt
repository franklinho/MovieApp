package com.example.testdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = mockk<MovieRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uses route fallback when detail fetch fails`() = runTest {
        coEvery { repository.movie(1) } throws IOException("offline")

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle(
                movieId = 1,
                title = "Fallback title",
                backdropPath = "/fallback.jpg",
                overview = "Fallback overview",
            ),
            repository = repository,
        )

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals("Fallback title", uiState.movie?.title)
        assertEquals("/fallback.jpg", uiState.movie?.backdropPath)
        assertEquals("Fallback overview", uiState.movie?.overview)
    }

    @Test
    fun `successful detail fetch replaces route fallback`() = runTest {
        coEvery { repository.movie(1) } returns Movie(
            id = 1,
            title = "Fresh title",
            backdropPath = "/fresh.jpg",
            overview = "Fresh overview",
        )

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle(
                movieId = 1,
                title = "Fallback title",
                backdropPath = "/fallback.jpg",
                overview = "Fallback overview",
            ),
            repository = repository,
        )

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals("Fresh title", uiState.movie?.title)
        assertEquals("/fresh.jpg", uiState.movie?.backdropPath)
        assertEquals("Fresh overview", uiState.movie?.overview)
    }

    private fun savedStateHandle(
        movieId: Int,
        title: String?,
        backdropPath: String?,
        overview: String?,
    ): SavedStateHandle =
        SavedStateHandle(
            mapOf(
                "movieId" to movieId,
                "title" to title,
                "backdropPath" to backdropPath,
                "overview" to overview,
            )
        )
}
