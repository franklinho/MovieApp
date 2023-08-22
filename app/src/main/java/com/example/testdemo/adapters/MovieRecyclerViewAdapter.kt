package com.example.testdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.R
import com.example.testdemo.databinding.MovieRecyclerviewItemBinding
import com.example.testdemo.models.Movie

class MovieRecyclerViewAdapter(listener: MovieItemClickListener) : RecyclerView.Adapter<MovieAdapterViewHolder>() {
    var diffUtilCallback: DiffUtil.ItemCallback<Movie> = DiffCallback()
    private val mAsyncListDiffer = AsyncListDiffer(this, diffUtilCallback)
    private val listener = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = MovieRecyclerviewItemBinding.inflate(inflater)
        val view = binding.root
        return MovieAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mAsyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        val movie = mAsyncListDiffer.currentList[position]
        holder.textView.text = movie.title
        holder.view.setOnClickListener { listener.onMovieItemClicked(movie)}
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
    val textView: TextView
    val view: View

    init {
        view = parentView
        textView = parentView.findViewById(R.id.textView)
    }
}