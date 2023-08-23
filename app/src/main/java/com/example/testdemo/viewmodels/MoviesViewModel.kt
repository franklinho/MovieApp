package com.example.testdemo.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.room.Database
import com.example.testdemo.data.DatabaseBuilder
import com.example.testdemo.data.DatabaseHelperImpl
import com.example.testdemo.fragments.MainFragmentDirections
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.parceler.Parcels


class MoviesViewModel(context: Context) : ViewModel() {
    private val movieService: MovieService = MovieService()
    private val movieApi: MovieApi = movieService.movieApi
    private val moviesStateFlow: MutableStateFlow<List<Movie>> = MutableStateFlow(ArrayList())
    private val dbHelperImpl = DatabaseHelperImpl(DatabaseBuilder.getInstance(context))

    fun requestTrendingMovies() {
        //TODO: Cache trending movies for 24 hours
//        viewModelScope.launch {
//            dbHelperImpl
//                .getMovies()
//                .flowOn(Dispatchers.IO)
//                .catch {
//                    Log.d( "MovieService/TrendingApi", "Unable to get movies from DB")
//                    requestMoviesFromServer(null)
//                }.collect {
//                    if (it.isEmpty()) {
//                        Log.d( "MovieService/TrendingApi", "No movies in DB")
//                        requestMoviesFromServer(null)
//                    } else {
//                        Log.d( "MovieService/TrendingApi", "Successfully grabbed movies from DB")
//                        updateMovies(it.map { movie -> Movie.fromObject(movie) })
//                    }
//                }
//            }
        requestMoviesFromServer(null)
    }

    private fun updateMovies(movieList : List<Movie>) {
        CoroutineScope(Dispatchers.Main).launch {
            moviesStateFlow.value = movieList
        }
    }

    fun searchMovies(query: String?) {
        requestMoviesFromServer(query)
    }

    private fun requestMoviesFromServer(query : String ?) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = if (query == null) movieApi.trendingMovies else movieApi.searchMovies(query)
            val tag = if (query == null) "MovieService/TrendingApi" else "MovieService/SearchAPI"

            try {
                val response = call?.execute()

                if (response == null || !response.isSuccessful) {
                    //unsuccessful
                    Log.d( tag, "Response Unsuccessful")
                }

                //successful
                val movieList: List<Movie>? = response?.body()?.results
                if (movieList != null) {
//                    if (query == null) {
//                        viewModelScope.launch { dbHelperImpl.deleteAll().flowOn(Dispatchers.IO).collect {
//                            dbHelperImpl.insertAll(movieList.map { com.example.testdemo.data.Movie.fromObject(it) }).flowOn(Dispatchers.IO).collect {
//                                Log.d(tag, "Inserted movie into DB")
//                                updateMovies(movieList)
//                            }
//                        } }
//                    } else {
                        updateMovies(movieList)
//                    }
                }
            } catch (t: Throwable) {
                //failed
                Log.d(tag, t.localizedMessage)
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
