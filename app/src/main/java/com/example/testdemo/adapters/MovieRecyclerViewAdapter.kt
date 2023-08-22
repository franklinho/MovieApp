package com.example.testdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.R
import com.example.testdemo.models.Movie

class MovieRecyclerViewAdapter(listener: MovieItemClickListener) : RecyclerView.Adapter<MovieAdapterViewHolder>() {
    private val movies = mutableListOf<Movie>()
    private val listener = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_recyclerview_item, parent, false)
        return MovieAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        val movie = movies[position]
        holder.textView.text = movie.title
        holder.view.setOnClickListener { listener.onMovieItemClicked(movie)}
    }

    fun updateData(data: List<Movie>) {
        val previousContentSize = movies.size
        movies.clear()
        notifyItemRangeRemoved(0, previousContentSize)
        movies.addAll(data)
        notifyItemRangeInserted(0, data.size)
    }

}

interface MovieItemClickListener {
    fun onMovieItemClicked(movie : Movie)
}

class MovieAdapterViewHolder(parentView : View) : RecyclerView.ViewHolder(parentView) {
    val textView: TextView
    val view: View

    init {
        view = parentView
        textView = parentView.findViewById(R.id.textView)
    }
}