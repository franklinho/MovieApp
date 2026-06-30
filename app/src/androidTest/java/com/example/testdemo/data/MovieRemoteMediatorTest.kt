package com.example.testdemo.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.testdemo.models.Movie as MovieDto
import com.example.testdemo.models.MoviesResponse
import com.example.testdemo.networking.MovieApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var remoteKeyDao: MovieRemoteKeyDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movieDao = database.movieDao()
        remoteKeyDao = database.movieRemoteKeyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshClearsOldCacheAndWritesFirstPageKeys() = runBlocking {
        movieDao.insertAll(listOf(movieEntity(id = 99, orderIndex = 0)))
        remoteKeyDao.insertAll(listOf(MovieRemoteKey(movieId = 99, prevKey = null, nextKey = 2)))
        val api = FakeMovieApi(
            1 to MoviesResponse(
                page = 1,
                totalPages = 2,
                results = listOf(movieDto(id = 1), movieDto(id = 2)),
            )
        )

        val result = mediator(api).load(LoadType.REFRESH, emptyPagingState())

        assertSuccess(result, endOfPaginationReached = false)
        assertEquals(listOf(1), api.requestedPages)
        assertEquals(2, movieDao.count())
        assertNull(remoteKeyDao.remoteKeyByMovieId(99))
        assertEquals(MovieRemoteKey(movieId = 1, prevKey = null, nextKey = 2), remoteKeyDao.remoteKeyByMovieId(1))
        assertEquals(MovieRemoteKey(movieId = 2, prevKey = null, nextKey = 2), remoteKeyDao.remoteKeyByMovieId(2))
    }

    @Test
    fun appendUsesLastRemoteKeyAndMarksEndOfPagination() = runBlocking {
        val cachedMovie = movieEntity(id = 1, orderIndex = 0)
        movieDao.insertAll(listOf(cachedMovie))
        remoteKeyDao.insertAll(listOf(MovieRemoteKey(movieId = 1, prevKey = null, nextKey = 2)))
        val api = FakeMovieApi(
            2 to MoviesResponse(
                page = 2,
                totalPages = 2,
                results = listOf(movieDto(id = 3)),
            )
        )

        val result = mediator(api).load(
            LoadType.APPEND,
            pagingStateWithPage(listOf(cachedMovie)),
        )

        assertSuccess(result, endOfPaginationReached = true)
        assertEquals(listOf(2), api.requestedPages)
        assertEquals(2, movieDao.count())
        assertEquals(MovieRemoteKey(movieId = 3, prevKey = 1, nextKey = null), remoteKeyDao.remoteKeyByMovieId(3))
    }

    @Test
    fun refreshWithEndPageAnchorUsesPreviousKey() = runBlocking {
        val cachedMovie = movieEntity(id = 3, orderIndex = 0)
        movieDao.insertAll(listOf(cachedMovie))
        remoteKeyDao.insertAll(listOf(MovieRemoteKey(movieId = 3, prevKey = 1, nextKey = null)))
        val api = FakeMovieApi(
            2 to MoviesResponse(
                page = 2,
                totalPages = 2,
                results = listOf(movieDto(id = 3)),
            )
        )

        val result = mediator(api).load(
            LoadType.REFRESH,
            pagingStateWithPage(listOf(cachedMovie), anchorPosition = 0),
        )

        assertSuccess(result, endOfPaginationReached = true)
        assertEquals(listOf(2), api.requestedPages)
    }

    private fun mediator(api: MovieApi): MovieRemoteMediator =
        MovieRemoteMediator(api, database, movieDao, remoteKeyDao)

    private fun emptyPagingState(): PagingState<Int, Movie> =
        PagingState(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = MovieRemoteMediator.PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

    private fun pagingStateWithPage(
        movies: List<Movie>,
        anchorPosition: Int? = null,
    ): PagingState<Int, Movie> =
        PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = movies,
                    prevKey = null,
                    nextKey = 2,
                )
            ),
            anchorPosition = anchorPosition,
            config = PagingConfig(pageSize = MovieRemoteMediator.PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

    private fun assertSuccess(
        result: RemoteMediator.MediatorResult,
        endOfPaginationReached: Boolean,
    ) {
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        result as RemoteMediator.MediatorResult.Success
        assertEquals(endOfPaginationReached, result.endOfPaginationReached)
    }

    private fun movieEntity(id: Int, orderIndex: Int): Movie =
        Movie(
            id = id,
            title = "Movie $id",
            overview = "Overview $id",
            isAdult = false,
            posterPath = "/poster$id.jpg",
            backdropPath = "/backdrop$id.jpg",
            orderIndex = orderIndex,
        )

    private fun movieDto(id: Int): MovieDto =
        MovieDto(
            id = id,
            title = "Movie $id",
            overview = "Overview $id",
            isAdult = false,
            posterPath = "/poster$id.jpg",
            backdropPath = "/backdrop$id.jpg",
        )

    private class FakeMovieApi(
        private vararg val trendingResponses: Pair<Int, MoviesResponse>,
    ) : MovieApi {
        val requestedPages = mutableListOf<Int>()

        override suspend fun trendingMovies(page: Int): MoviesResponse {
            requestedPages += page
            return trendingResponses.toMap().getValue(page)
        }

        override suspend fun searchMovies(query: String, page: Int): MoviesResponse =
            error("Search is not used by MovieRemoteMediator")
    }
}
