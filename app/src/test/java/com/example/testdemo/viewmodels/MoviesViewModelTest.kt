package com.example.testdemo.viewmodels

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.testdemo.MainDispatcherRule
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.models.Movie
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MovieRepository>()

    @Test
    fun `blank query loads trending immediately`() = runTest {
        every { repository.trendingPager() } returns flowOf(PagingData.empty<Movie>())
        every { repository.searchPager(any()) } returns flowOf(PagingData.empty())
        val viewModel = MoviesViewModel(repository)

        viewModel.movies.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { repository.trendingPager() }
        verify(exactly = 0) { repository.searchPager(any()) }
    }

    @Test
    fun `non blank query is trimmed and debounced before searching`() = runTest {
        every { repository.trendingPager() } returns flowOf(PagingData.empty<Movie>())
        every { repository.searchPager(any()) } returns flowOf(PagingData.empty())
        val viewModel = MoviesViewModel(repository)

        viewModel.movies.test {
            awaitItem()
            viewModel.updateSearchQuery("  bat")
            viewModel.updateSearchQuery("  batman  ")
            advanceTimeBy(299)
            verify(exactly = 0) { repository.searchPager(any()) }
            advanceTimeBy(1)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { repository.searchPager("batman") }
    }

    @Test
    fun `clearing query returns to trending immediately`() = runTest {
        every { repository.trendingPager() } returns flowOf(PagingData.empty<Movie>())
        every { repository.searchPager(any()) } returns flowOf(PagingData.empty())
        val viewModel = MoviesViewModel(repository)

        viewModel.movies.test {
            awaitItem()
            viewModel.updateSearchQuery("matrix")
            advanceTimeBy(300)
            awaitItem()
            viewModel.updateSearchQuery("")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 2) { repository.trendingPager() }
        verify(exactly = 1) { repository.searchPager("matrix") }
    }
}
