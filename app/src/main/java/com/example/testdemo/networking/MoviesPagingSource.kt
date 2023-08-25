package com.example.testdemo.networking

import android.net.http.HttpException
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testdemo.models.Movie
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 20

class MoviesPagingSource(private val service: MovieService) : PagingSource<Int, Movie>() {
    private val movieService: MovieService = MovieService()
    private val movieApi: MovieApi = movieService.movieApi

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        val call = movieApi.trendingMovies(pageIndex)
        val tag = "MovieService/TrendingApi"
        return try {
            val response = call?.execute()

            if (response == null || !response.isSuccessful) {
                //unsuccessful
                Log.d( tag, "Response Unsuccessful")
            }

            //successful
            val movieList: List<Movie> = response?.body()?.results ?: ArrayList()
            val nextKey = if (movieList.isNullOrEmpty()) null else pageIndex + (params.loadSize/ NETWORK_PAGE_SIZE)
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