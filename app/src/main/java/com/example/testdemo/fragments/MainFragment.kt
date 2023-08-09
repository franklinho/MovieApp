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


class MainFragment : Fragment(), MovieItemClickListener {
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
        movieAdapter.updateData(listOf("Batman", "The Godfather", "Star Wars", "Indiana Jones"))
    }

    override fun onMovieItemClicked(movieTitle: String) {
        Log.d("DEBUG", movieTitle)
        val action = MainFragmentDirections.actionMainFragmentToItemFragment(movieTitle)
        this.requireView().findNavController().navigate(action)
    }


}