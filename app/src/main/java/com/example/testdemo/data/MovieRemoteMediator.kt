package com.example.testdemo.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.testdemo.networking.MovieApi
import retrofit2.HttpException
import java.io.IOException

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
                LoadType.APPEND -> movieDao.nextTrendingPage()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = movieApi.trendingMovies(page)
            val movies = response.results ?: emptyList()
            val endReached = movies.isEmpty() || page >= response.totalPages
            val nextPage = if (endReached) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.clearAll()
                    movieDao.clearTrendingRemoteKey()
                }
                val start = (movieDao.maxOrderIndex() ?: -1) + 1
                movieDao.insertAll(movies.mapIndexed { index, movie ->
                    movie.toEntity(orderIndex = start + index)
                })
                movieDao.upsertTrendingRemoteKey(TrendingRemoteKey(nextPage = nextPage))
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
