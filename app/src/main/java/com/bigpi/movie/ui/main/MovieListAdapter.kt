package com.bigpi.movie.ui.main

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigpi.movie.R
import com.bigpi.movie.databinding.ItemMovieBinding
import com.bigpi.movie.databinding.ItemSeparatorBinding
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.ext.getDataBinding
import com.bigpi.movie.ui.main.model.MoviePresentation


class MovieListAdapter: PagingDataAdapter<MoviePresentation, RecyclerView.ViewHolder>(ItemDiffCallback()) {
    private var listener: ((movie: Movie, binding: ItemMovieBinding) -> Unit)? = null

    abstract class MovieModelViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(model: MoviePresentation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MoviePresentation.Type.MovieItem.ordinal -> MovieViewHolder(getDataBinding(parent, R.layout.item_movie))
            MoviePresentation.Type.SeparatorItem.ordinal -> SeparatorViewHolder(getDataBinding(parent, R.layout.item_separator))
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as MovieModelViewHolder).bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.ordinal ?: run { throw UnsupportedOperationException("Unknown view") }
    }

    fun setAdapterListeners(listener: ((movie: Movie, binding: ItemMovieBinding) -> Unit)?) {
        this.listener = listener
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        MovieModelViewHolder(binding) {

        override fun bind(model: MoviePresentation) {
            val movieModel = (model as MoviePresentation.MovieItem)

            binding.movie = movieModel.movie
            listener?.invoke(movieModel.movie, binding)
            binding.executePendingBindings()
        }
    }

    inner class SeparatorViewHolder(private val binding: ItemSeparatorBinding) :
        MovieModelViewHolder(binding) {
        override fun bind(model: MoviePresentation) {
            binding.executePendingBindings()
        }
    }

    private class ItemDiffCallback: DiffUtil.ItemCallback<MoviePresentation>() {
        override fun areItemsTheSame(oldItem: MoviePresentation, newItem: MoviePresentation): Boolean {

            val movieDiff = oldItem is MoviePresentation.MovieItem
                    && newItem is MoviePresentation.MovieItem
                    && oldItem.movie.title == newItem.movie.title
                    && oldItem.movie.bookmark == newItem.movie.bookmark

            val separatorDiff = oldItem is MoviePresentation.SeparatorItem
                    && newItem is MoviePresentation.SeparatorItem
                    && oldItem == newItem

            return movieDiff || separatorDiff
        }

        override fun areContentsTheSame(oldItem: MoviePresentation, newItem: MoviePresentation): Boolean {
            return oldItem == newItem
        }
    }
}