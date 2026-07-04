package com.example.testdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testdemo.R
import com.example.testdemo.models.Movie
import com.example.testdemo.ui.MovieListScreen
import com.example.testdemo.ui.theme.TestDemoTheme
import com.example.testdemo.viewmodels.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val moviesViewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            TestDemoTheme {
                MovieListScreen(
                    viewModel = moviesViewModel,
                    onMovieClick = ::openMovieDetail,
                )
            }
        }
    }

    private fun openMovieDetail(movie: Movie) {
        findNavController().navigate(
            R.id.action_mainFragment_to_itemFragment,
            bundleOf(
                ARG_MOVIE_TITLE to movie.title,
                ARG_MOVIE_POSTER to movie.backdropPath,
                ARG_MOVIE_OVERVIEW to movie.overview,
            ),
        )
    }

    companion object {
        const val ARG_MOVIE_TITLE = "movieTitle"
        const val ARG_MOVIE_POSTER = "moviePoster"
        const val ARG_MOVIE_OVERVIEW = "movieOverview"
    }
}
