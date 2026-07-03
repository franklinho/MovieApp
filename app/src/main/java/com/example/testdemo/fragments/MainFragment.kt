package com.example.testdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
                    onMovieClick = { movie ->
                        moviesViewModel.launchMovieFragment(this@MainFragment, movie)
                    },
                )
            }
        }
    }
}
