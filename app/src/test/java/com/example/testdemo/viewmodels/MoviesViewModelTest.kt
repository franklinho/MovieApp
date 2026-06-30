package com.example.testdemo.viewmodels

import app.cash.turbine.test
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `emits Success from cached trending movies`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = mockk<MovieRepository>(relaxed = true)
        val movies = listOf(Movie(id = 1, title = "Inception"))
        every { repository.trendingMovies() } returns flowOf(movies)
        coEvery { repository.refreshTrending() } returns Unit

        val vm = MoviesViewModel(repository)

        vm.uiState.test {
            assertEquals(MoviesUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(MoviesUiState.Success(movies), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when cache empty and refresh fails`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = mockk<MovieRepository>(relaxed = true)
        every { repository.trendingMovies() } returns flowOf(emptyList())
        coEvery { repository.refreshTrending() } throws RuntimeException("boom")

        val vm = MoviesViewModel(repository)

        vm.uiState.test {
            assertEquals(MoviesUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is MoviesUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
