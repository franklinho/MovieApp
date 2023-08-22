package com.example.testdemo.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val etSearch: EditText? = view.requireViewById(R.id.etSearch)
        etSearch!!.setOnEditorActionListener { textView, _, _ ->
            val query = textView.text.toString()
            if (query.isNotEmpty()) {
                moviesViewModel.searchMovies(query)
            } else {
                moviesViewModel.requestTrendingMovies()
            }
            val imm = this.requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            false
        }

        val movieAdapter = MovieRecyclerViewAdapter(this)
        val recyclerView: RecyclerView = view.requireViewById(R.id.rvRecycler)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = movieAdapter
        if (!moviesViewModel.hasMovies()) {moviesViewModel.requestTrendingMovies()}
        CoroutineScope(Dispatchers.Main).launch {
            moviesViewModel.getMoviesStateFlow().collect {
                movieAdapter.updateData(it)
            }
        }
        return view
    }

    override fun onMovieItemClicked(movie: Movie) {
        moviesViewModel.launchMovieFragment(this, movie)
    }


}