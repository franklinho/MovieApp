package com.example.testdemo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.R
import com.example.testdemo.adapters.MovieItemClickListener
import com.example.testdemo.adapters.MovieRecyclerViewAdapter
import com.example.testdemo.models.Movie
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainFragment : Fragment(), MovieItemClickListener {
    private var moviesViewModel : MoviesViewModel = MoviesViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieAdapter = MovieRecyclerViewAdapter(this)
        val recyclerView: RecyclerView = view.findViewById(R.id.movie_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = movieAdapter
        moviesViewModel.requestTrendingMovies()
        CoroutineScope(Dispatchers.Main).launch {
            moviesViewModel.getMoviesStateFlow().collect {
                movieAdapter.updateData(it)
            }
        }
    }

    override fun onMovieItemClicked(movie: Movie) {
        moviesViewModel.launchMovieFragment(this, movie)
    }


}