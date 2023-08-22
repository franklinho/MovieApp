package com.example.testdemo.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        if (movie.posterPath != null) {
            Glide
                .with(holder.ivMoviePoster!!.context)
                .load(MovieService.getFullImageUrl(movie.posterPath))
                .into(holder.ivMoviePoster)
        } else {
            holder.ivMoviePoster.visibility = View.GONE
            holder.tvMovieTitle.visibility = View.VISIBLE
            holder.tvMovieTitle.text = movie.title
        }
    }
    override fun onViewRecycled(holder: MovieAdapterViewHolder) {
        super.onViewRecycled(holder)
        holder.tvMovieTitle.text = null
        holder.tvMovieTitle.visibility = View.GONE
        holder.ivMoviePoster.setImageURI(null)
        holder.ivMoviePoster.visibility = View.VISIBLE
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
    var tvMovieTitle: TextView
    val view: View

    init {
        view = parentView
        ivMoviePoster = itemView.requireViewById(R.id.ivMoviePoster) as ImageView
        tvMovieTitle = itemView.requireViewById(R.id.tvMovieTitle) as TextView
    }
}