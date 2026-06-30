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
 * from TMDB and writes them through. Remote keys track pagination independently from the
 * current cached row count.
 */
@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val movieApi: MovieApi,
    private val database: AppDatabase,
    private val movieDao: MovieDao,
    private val movieRemoteKeyDao: MovieRemoteKeyDao,
) : RemoteMediator<Int, Movie>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> remoteKeyClosestToCurrentPosition(state)?.let { remoteKey ->
                    remoteKey.nextKey?.minus(1) ?: remoteKey.prevKey?.plus(1)
                } ?: 1
                LoadType.PREPEND -> {
                    val prevKey = remoteKeyForFirstItem(state)?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    prevKey
                }
                LoadType.APPEND -> {
                    val nextKey = remoteKeyForLastItem(state)?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextKey
                }
            }

            val response = movieApi.trendingMovies(page)
            val movies = response.results ?: emptyList()
            val endReached = movies.isEmpty() || page >= response.totalPages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieRemoteKeyDao.clearAll()
                    movieDao.clearAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endReached) null else page + 1
                val start = movieDao.count()
                movieRemoteKeyDao.insertAll(
                    movies.map { dto ->
                        MovieRemoteKey(movieId = dto.id, prevKey = prevKey, nextKey = nextKey)
                    }
                )
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

    private suspend fun remoteKeyForLastItem(state: PagingState<Int, Movie>): MovieRemoteKey? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { movie ->
            movieRemoteKeyDao.remoteKeyByMovieId(movie.id)
        }

    private suspend fun remoteKeyForFirstItem(state: PagingState<Int, Movie>): MovieRemoteKey? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { movie ->
            movieRemoteKeyDao.remoteKeyByMovieId(movie.id)
        }

    private suspend fun remoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Movie>,
    ): MovieRemoteKey? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movieId ->
                movieRemoteKeyDao.remoteKeyByMovieId(movieId)
            }
        }
}
