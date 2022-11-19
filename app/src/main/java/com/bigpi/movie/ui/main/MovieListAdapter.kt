package com.bigpi.movie.ui.main

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bigpi.movie.R
import com.bigpi.movie.databinding.ItemMovieBinding
import com.bigpi.movie.databinding.ItemPagingStateBinding
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.ApiFailure
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.ext.getDataBinding
import com.bigpi.movie.ui.main.model.MovieListUi
import com.bigpi.movie.ui.main.model.MovieUiState
import com.bigpi.movie.ui.main.model.mapper.mapToDomain


class MovieListAdapter: ListAdapter<MovieListUi, MovieListAdapter.MovieModelViewHolder<MovieListUi>>(ItemDiffCallback()) {
    private var movieAdapterListener: ((movie: MovieItem, binding: ItemMovieBinding) -> Unit)? = null
    private var loadingStateListener: ((binding: ItemPagingStateBinding) -> Unit)? = null
    private val dataList = mutableListOf<MovieListUi>()

    abstract class MovieModelViewHolder<ITEM: MovieListUi>(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(model: ITEM)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieModelViewHolder<MovieListUi> {
        return when(viewType) {
            MovieListUi.Type.MovieItem.ordinal -> MovieViewHolder(getDataBinding(parent, R.layout.item_movie))
            MovieListUi.Type.PagingState.ordinal -> PagingStateViewHolder(getDataBinding(parent, R.layout.item_paging_state))
            else -> throw UnsupportedOperationException("Unknown view")
        } as MovieModelViewHolder<MovieListUi>
    }

    override fun onBindViewHolder(holder: MovieModelViewHolder<MovieListUi>, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.ordinal ?: run { throw UnsupportedOperationException("Unknown view") }
    }

    fun setMovieItemAdapterListener(listener: ((movie: MovieItem, binding: ItemMovieBinding) -> Unit)?) {
        this.movieAdapterListener = listener
    }

    fun setRetryListener(listener: ((binding: ItemPagingStateBinding) -> Unit)?) {
        this.loadingStateListener = listener
    }

    fun updateBookmarkItem(movieItem: MovieListUi.MovieItemUi) {
        dataList
            .indexOfFirst {
                val oldItem = (it as? MovieListUi.MovieItemUi)
                oldItem?.let { old ->
                    if(old == movieItem) {
                        false
                    }
                    else {
                        old.link == movieItem.link
                    }
                } ?: kotlin.run {
                    false
                }
            }
            .takeIf { it > -1 }
            ?.run {
            dataList[this] = movieItem
            notifyItemChanged(this)
        }
    }

    fun setLoadingState(state: MovieUiState){
        dataList
            .indexOfFirst {
                it is MovieListUi.PagingStateUi
            }
            .takeIf { it > -1 }
            ?.run {
                dataList[this] = MovieListUi.PagingStateUi(
                    state = if(state.isLoading) {
                        MovieListUi.PagingStateUi.PagingState.Loading
                    }
                    else {
                        MovieListUi.PagingStateUi.PagingState.Loading
                    }
                )
                notifyItemChanged(this)
            }
    }

    fun submitMovieItems(movie: List<MovieListUi.MovieItemUi>, hasMoreLoads: Boolean) {

        if(dataList.containsAll(movie)) {
            return
        }

        dataList
            .indexOfFirst {
                it is MovieListUi.PagingStateUi
            }
            .takeIf { it > -1 }
            ?.run {
                dataList.removeAt(this)
                notifyItemRemoved(this)
            }

        dataList.addAll(movie)

        if(hasMoreLoads) {
            dataList.add(MovieListUi.PagingStateUi())
        }

        super.submitList(dataList)
    }

    fun reset() {
        dataList.clear()
        submitList(null)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        MovieModelViewHolder<MovieListUi.MovieItemUi>(binding) {

        override fun bind(model: MovieListUi.MovieItemUi) {
                binding.movieItem = model.mapToDomain()
                movieAdapterListener?.invoke(model.mapToDomain(), binding)

            binding.executePendingBindings()
        }
    }

    inner class PagingStateViewHolder(private val binding: ItemPagingStateBinding) :
        MovieModelViewHolder<MovieListUi.PagingStateUi>(binding) {
        override fun bind(model: MovieListUi.PagingStateUi) {
                binding.state = model.state

            binding.btnRetry.setOnClickListener {
                binding.state = MovieListUi.PagingStateUi.PagingState.Loading
                loadingStateListener?.invoke(binding)
            }
            binding.executePendingBindings()
        }
    }

    private class ItemDiffCallback: DiffUtil.ItemCallback<MovieListUi>() {
        override fun areItemsTheSame(oldItem: MovieListUi, newItem: MovieListUi): Boolean {

            val movieDiff = oldItem is MovieListUi.MovieItemUi
                    && newItem is MovieListUi.MovieItemUi
                    && oldItem.title == newItem.title
                    && oldItem.bookmark == newItem.bookmark

            val separatorDiff = oldItem is MovieListUi.PagingStateUi
                    && newItem is MovieListUi.PagingStateUi
                    && oldItem == newItem

            return movieDiff || separatorDiff
        }

        override fun areContentsTheSame(oldItem: MovieListUi, newItem: MovieListUi): Boolean {
            return oldItem == newItem
        }
    }
}