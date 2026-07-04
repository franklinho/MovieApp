package com.example.testdemo.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.testdemo.networking.MovieApi
import retrofit2.HttpException
import java.io.IOException

/**
 * Paging 3 cache-through: Room is the single source of truth, this mediator fetches pages
 * from TMDB and writes them through. Next page is derived from the cached count (TMDB pages
 * are sequential, append-only), so no separate remote-keys table is needed.
 */
@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val movieApi: MovieApi,
    private val database: AppDatabase,
    private val movieDao: MovieDao,
) : RemoteMediator<Int, MovieDto>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieDto>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> (movieDao.count() / PAGE_SIZE) + 1
            }

            val response = movieApi.trendingMovies(page)
            val movies = response.results ?: emptyList()
            val endReached = movies.isEmpty() || page >= response.totalPages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) movieDao.clearAll()
                val start = movieDao.count()
                movieDao.insertAll(movies.mapIndexed { i, dto -> dto.toEntity(orderIndex = start + i) })
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
