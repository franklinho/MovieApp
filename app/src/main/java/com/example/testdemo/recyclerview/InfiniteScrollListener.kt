package com.example.testdemo.recyclerview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A scroll listener for RecyclerView to load more items as you approach the end.
 *
 * Adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
abstract class InfiniteScrollListener(
    private val layoutManager: GridLayoutManager
) : RecyclerView.OnScrollListener() {
    private val loadMoreRunnable = Runnable { onLoadMore() }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // bail out if scrolling upward or already loading data
        if (dy < 0 || isDataLoading()) return

        val visibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount

        if (totalItemCount <= visibleItemCount + VISIBLE_THRESHOLD) {
            recyclerView.post(loadMoreRunnable)
        }
    }

    abstract fun onLoadMore()

    abstract fun isDataLoading(): Boolean

    companion object {
        // The minimum number of items remaining before we should loading more.
        private const val VISIBLE_THRESHOLD = 3
    }
}