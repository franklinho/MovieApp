package com.example.testdemo.networking

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testdemo.models.Movie
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 20

class MoviesPagingSource(private val service: MovieService) : PagingSource<Int, Movie>() {
    private val movieApi: MovieApi = service.movieApi

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        return try {
            val movieList: List<Movie> = movieApi.trendingMovies(pageIndex).results ?: emptyList()
            val nextKey = if (movieList.isEmpty()) null else pageIndex + (params.loadSize / NETWORK_PAGE_SIZE)
            return LoadResult.Page(
                data = movieList,
                prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
