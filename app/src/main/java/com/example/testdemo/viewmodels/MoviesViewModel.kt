package com.example.testdemo.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.parceler.Parcels


class MoviesViewModel : ViewModel() {
    private val movieService: MovieService = MovieService()
    private val movieApi: MovieApi = movieService.movieApi
    private val moviesStateFlow: MutableStateFlow<List<Movie>> = MutableStateFlow(ArrayList())

    fun requestTrendingMovies() {
        //TODO: Cache trending movies for 24 hours

        CoroutineScope(Dispatchers.IO).launch {
            val call = movieApi.trendingMovies

            try {
                val response = call?.execute()

                if (response == null || !response.isSuccessful) {
                    //unsuccessful
                    Log.d("MovieService/TrendingApi", "Response Unsuccessful")
                }

                //successful
                CoroutineScope(Dispatchers.Main).launch {
                    val movieList: List<Movie>? = response?.body()?.results
                    if (movieList != null) {
                        moviesStateFlow.value = movieList
                    }
                }
            } catch (t: Throwable) {
                //failed
                Log.d("MovieService/TrendingApi", t.localizedMessage)
            }
        }
    }

    fun searchMovies(query: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = movieApi.searchMovies(query)

            try {
                val response = call?.execute()

                if (response == null || !response.isSuccessful) {
                    //unsuccessful
                    Log.d("MovieService/SearchAPI", "Response Unsuccessful")
                }

                //successful
                CoroutineScope(Dispatchers.Main).launch {
                    val movieList: List<Movie>? = response?.body()?.results
                    if (movieList != null) {
                        moviesStateFlow.value = movieList
                    }
                }
            } catch (t: Throwable) {
                //failed
                Log.d("MovieService/SearchAPI", t.localizedMessage)
            }
        }
    }

    fun getMoviesStateFlow(): MutableStateFlow<List<Movie>> {
        return moviesStateFlow
    }

    fun hasMovies(): Boolean { return moviesStateFlow.value.isNotEmpty()}

    fun launchMovieFragment(fragment: Fragment, movie: Movie) {
        //TODO: Add shared element transition
        val action = MainFragmentDirections.actionMainFragmentToItemFragment(movie.title, movie.backdropPath, movie.overview)
        fragment.requireView().findNavController().navigate(action)
    }
}
