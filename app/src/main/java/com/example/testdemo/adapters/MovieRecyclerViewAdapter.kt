package com.example.testdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testdemo.R
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieService

class MovieRecyclerViewAdapter(
    private val listener: MovieItemClickListener,
) : PagingDataAdapter<Movie, MovieAdapterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_recyclerview_item, parent, false)
        return MovieAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        val movie = getItem(position) ?: return
        holder.view.setOnClickListener { listener.onMovieItemClicked(movie) }
        val posterPath = movie.posterPath
        if (posterPath != null) {
            holder.tvMovieTitle.visibility = View.GONE
            holder.ivMoviePoster.visibility = View.VISIBLE
            Glide.with(holder.ivMoviePoster.context)
                .load(MovieService.getFullImageUrl(posterPath))
                .into(holder.ivMoviePoster)
        } else {
            holder.ivMoviePoster.visibility = View.GONE
            holder.tvMovieTitle.visibility = View.VISIBLE
            holder.tvMovieTitle.text = movie.title
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
    }
}

interface MovieItemClickListener {
    fun onMovieItemClicked(movie: Movie)
}

class MovieAdapterViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
    val view: View = parentView
    val ivMoviePoster: ImageView = parentView.findViewById(R.id.ivMoviePoster)
    val tvMovieTitle: TextView = parentView.findViewById(R.id.tvMovieTitle)
}
