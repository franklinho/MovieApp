package com.example.testdemo.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.R
import com.example.testdemo.adapters.MovieItemClickListener
import com.example.testdemo.adapters.MovieRecyclerViewAdapter
import com.example.testdemo.models.Movie
import com.example.testdemo.viewmodels.MoviesUiState
import com.example.testdemo.viewmodels.MoviesViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment(), MovieItemClickListener {

    private val moviesViewModel: MoviesViewModel by viewModels()
    private lateinit var movieAdapter: MovieRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        etSearch.setOnEditorActionListener { textView, _, _ ->
            moviesViewModel.searchMovies(textView.text.toString())
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            false
        }

        movieAdapter = MovieRecyclerViewAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvRecycler)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = movieAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                moviesViewModel.uiState.collect { state ->
                    when (state) {
                        is MoviesUiState.Success -> movieAdapter.updateData(state.movies)
                        is MoviesUiState.Error -> { /* TODO(Slice 8): surface error UI */ }
                        MoviesUiState.Loading -> { /* TODO(Slice 8): surface loading UI */ }
                    }
                }
            }
        }
    }

    override fun onMovieItemClicked(movie: Movie) {
        moviesViewModel.launchMovieFragment(this, movie)
    }
}
