package com.example.testdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testdemo.R
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieService

class MovieRecyclerViewAdapter(listener: MovieItemClickListener) : RecyclerView.Adapter<MovieAdapterViewHolder>() {
    var diffUtilCallback: DiffUtil.ItemCallback<Movie> = DiffCallback()
    private val mAsyncListDiffer = AsyncListDiffer(this, diffUtilCallback)
    private val listener = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_recyclerview_item, parent, false)
        return MovieAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mAsyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        val movie = mAsyncListDiffer.currentList[position]
        holder.view.setOnClickListener { listener.onMovieItemClicked(movie)}

        movie.posterPath?.let {
            Glide
                .with(holder.ivMoviePoster!!.context)
                .load(MovieService.getFullImageUrl(movie.posterPath))
                .into(holder.ivMoviePoster)
        }
    }

    fun updateData(data: List<Movie>) {
        mAsyncListDiffer.submitList(data)
    }

}

interface MovieItemClickListener {
    fun onMovieItemClicked(movie : Movie)
}

//TODO: Add pagination
private class DiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id === newItem.id
    }
}

class MovieAdapterViewHolder(parentView : View) : RecyclerView.ViewHolder(parentView) {
    var ivMoviePoster: ImageView
    val view: View

    init {
        view = parentView
        ivMoviePoster = itemView.requireViewById(R.id.ivMoviePoster) as ImageView
    }
}