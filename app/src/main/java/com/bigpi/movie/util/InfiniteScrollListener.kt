package com.bigpi.movie.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class InfiniteScrollListener(
    private val startIndex: Int,
    private val pageSize: Int
) : RecyclerView.OnScrollListener() {

    var start = startIndex

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        recyclerView.adapter?.let { adapter ->
            val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val itemTotalCount = adapter.itemCount - 1

            if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                start += pageSize
                onLoadMore(start = start)
            }
        }
    }

    fun reset() {
        start = startIndex
    }

    fun retry() {
        onLoadMore(start = start)
    }

    abstract fun onLoadMore(start: Int)
}