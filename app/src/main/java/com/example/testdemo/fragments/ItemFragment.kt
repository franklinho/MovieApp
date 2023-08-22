package com.example.testdemo.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testdemo.databinding.FragmentItemBinding
import com.example.testdemo.networking.MovieService
import org.parceler.Parcels

class ItemFragment : Fragment() {
    private val args : ItemFragmentArgs by navArgs()
    private lateinit var binding: FragmentItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val textView : TextView = view.findViewById(R.id.movie_title_textview)
//        textView.text = args.movieTitle

        val ivMoviePoster = binding!!.ivMoviePoster
        val tvTitle = binding!!.tvTitle
        val tvOverview = binding!!.tvOverview

//        Glide
//            .with(this)
//            .load(MovieService.getFullImageUrl(args.moviePoster)
//            .into(ivMoviePoster)

        tvTitle.text = args.movieTitle
        tvOverview.text = args.movieOverview
    }
}