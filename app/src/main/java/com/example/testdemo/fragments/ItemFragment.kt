package com.example.testdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testdemo.ui.MovieDetailScreen
import com.example.testdemo.ui.theme.TestDemoTheme

class ItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            TestDemoTheme {
                MovieDetailScreen(
                    title = requireArguments().getString(MainFragment.ARG_MOVIE_TITLE),
                    backdropPath = requireArguments().getString(MainFragment.ARG_MOVIE_POSTER),
                    overview = requireArguments().getString(MainFragment.ARG_MOVIE_OVERVIEW),
                    onBack = { findNavController().popBackStack() },
                )
            }
        }
    }
}
