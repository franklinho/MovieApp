package com.example.testdemo.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.SearchPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.testdemo.models.Movie as MovieDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Slice 5: trending is a Paging 3 stream backed by Room (via [MovieRemoteMediator]); search
 * is a separate network-only paged stream. Both surface as `Flow<PagingData<MovieDto>>`.
 */
@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val database: AppDatabase,
    private val movieDao: MovieDao,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun trendingPager(): Flow<PagingData<MovieDto>> = Pager(
        config = PagingConfig(pageSize = MovieRemoteMediator.PAGE_SIZE),
        remoteMediator = MovieRemoteMediator(movieApi, database, movieDao),
        pagingSourceFactory = { movieDao.pagingSource() },
    ).flow.map { pagingData -> pagingData.map { it.toDto() } }

    fun searchPager(query: String): Flow<PagingData<MovieDto>> = Pager(
        config = PagingConfig(pageSize = MovieRemoteMediator.PAGE_SIZE),
        pagingSourceFactory = { SearchPagingSource(movieApi, query) },
    ).flow
}
