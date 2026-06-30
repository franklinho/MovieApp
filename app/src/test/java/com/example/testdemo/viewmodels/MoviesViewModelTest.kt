package com.example.testdemo.viewmodels

import app.cash.turbine.test
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun `emits Loading then Success when repository returns movies`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = mockk<MovieRepository>()
        val movies = listOf(Movie(id = 1, title = "Inception"))
        coEvery { repository.trendingMovies(1) } returns movies

        val vm = MoviesViewModel(repository)

        vm.uiState.test {
            assertEquals(MoviesUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(MoviesUiState.Success(movies), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when repository throws`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = mockk<MovieRepository>()
        coEvery { repository.trendingMovies(1) } throws RuntimeException("boom")

        val vm = MoviesViewModel(repository)

        vm.uiState.test {
            assertEquals(MoviesUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is MoviesUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
