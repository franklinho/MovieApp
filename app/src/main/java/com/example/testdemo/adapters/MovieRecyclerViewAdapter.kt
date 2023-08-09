package com.example.testdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.R

class MovieRecyclerViewAdapter(listener: MovieItemClickListener) : RecyclerView.Adapter<MovieAdapterViewHolder>() {
    private val movies = mutableListOf<String>()
    private val listener = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_recyclerview_item, parent, false)
        return MovieAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        val title = movies[position]
        holder.textView.text = title
        holder.view.setOnClickListener { listener.onMovieItemClicked(title)}
    }

    fun updateData(data: List<String>) {
        movies.clear()
        movies.addAll(data)
        notifyItemRangeInserted(0, data.size)
    }

}

interface MovieItemClickListener {
    fun onMovieItemClicked(movieTitle : String)
}

class MovieAdapterViewHolder(parentView : View) : RecyclerView.ViewHolder(parentView) {
    val textView: TextView
    val view: View

    init {
        view = parentView
        textView = parentView.findViewById(R.id.textView)
    }
}