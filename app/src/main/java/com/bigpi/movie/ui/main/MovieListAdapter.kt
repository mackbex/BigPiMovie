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
import com.bigpi.movie.ui.main.model.MoviePresentation
import com.bigpi.movie.ui.main.model.mapper.mapToDomain


class MovieListAdapter: ListAdapter<MoviePresentation, RecyclerView.ViewHolder>(ItemDiffCallback()) {
    private var movieAdapterListener: ((movie: MovieItem, binding: ItemMovieBinding) -> Unit)? = null
    private var loadingStateListener: ((binding: ItemPagingStateBinding) -> Unit)? = null
    private val dataList = mutableListOf<MoviePresentation>()

    abstract class MovieModelViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(model: MoviePresentation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MoviePresentation.Type.MovieItem.ordinal -> MovieViewHolder(getDataBinding(parent, R.layout.item_movie))
            MoviePresentation.Type.PagingState.ordinal -> PagingStateViewHolder(getDataBinding(parent, R.layout.item_paging_state))
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as MovieModelViewHolder).bind(it) }
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

    fun updateBookmarkItem(movie: MoviePresentation.MovieItemPresent) {
        dataList
            .indexOfFirst {
                val oldItem = (it as? MoviePresentation.MovieItemPresent)
                oldItem?.let { old ->
                    if(old == movie) {
                        false
                    }
                    else {
                        old.link == movie.link
                    }
                } ?: kotlin.run {
                    false
                }
            }
            .takeIf { it > -1 }
            ?.run {
            dataList[this] = movie
            notifyItemChanged(this)
        }
    }

    fun setLoadingState(state: Resource<Any>){
        dataList
            .indexOfFirst {
                it is MoviePresentation.PagingStatePresent
            }
            .takeIf { it > -1 }
            ?.run {
                dataList[this] = MoviePresentation.PagingStatePresent(
                    when (state) {
                        is Resource.Success -> {
                            Resource.Success(null)
                        }
                        is Resource.Loading -> {
                            Resource.Loading
                        }
                        is Resource.Failure -> {
                            Resource.Failure(ApiFailure())
                        }
                    }
                )
                notifyItemChanged(this)
            }
    }

    fun submitMovieItems(movie: MoviePresentation.MoviePresent) {
        val movieList = movie.movieList
        if(dataList.containsAll(movieList)) {
            return
        }

        dataList
            .indexOfFirst {
                it is MoviePresentation.PagingStatePresent
            }
            .takeIf { it > -1 }
            ?.run {
                dataList.removeAt(this)
                notifyItemRemoved(this)
            }

        movie.run {
            dataList.addAll(movie.movieList)
        }

        if(movie.hasMoreLoads) {
            dataList.add(MoviePresentation.PagingStatePresent(state = Resource.Loading))
        }

        super.submitList(dataList)
    }

    fun reset() {
        dataList.clear()
        submitList(null)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        MovieModelViewHolder(binding) {

        override fun bind(model: MoviePresentation) {
            (model as? MoviePresentation.MovieItemPresent)?.mapToDomain()?.run {
                binding.movieItem = this
                movieAdapterListener?.invoke(this, binding)
            }
            binding.executePendingBindings()
        }
    }

    inner class PagingStateViewHolder(private val binding: ItemPagingStateBinding) :
        MovieModelViewHolder(binding) {
        override fun bind(model: MoviePresentation) {
            (model as? MoviePresentation.PagingStatePresent)?.run {
                binding.state = state
            }
            binding.btnRetry.setOnClickListener {
                binding.state = Resource.Loading
                loadingStateListener?.invoke(binding)
            }
            binding.executePendingBindings()
        }
    }

    private class ItemDiffCallback: DiffUtil.ItemCallback<MoviePresentation>() {
        override fun areItemsTheSame(oldItem: MoviePresentation, newItem: MoviePresentation): Boolean {

            val movieDiff = oldItem is MoviePresentation.MovieItemPresent
                    && newItem is MoviePresentation.MovieItemPresent
                    && oldItem.title == newItem.title
                    && oldItem.bookmark == newItem.bookmark

            val separatorDiff = oldItem is MoviePresentation.PagingStatePresent
                    && newItem is MoviePresentation.PagingStatePresent
                    && oldItem == newItem

            return movieDiff || separatorDiff
        }

        override fun areContentsTheSame(oldItem: MoviePresentation, newItem: MoviePresentation): Boolean {
            return oldItem == newItem
        }
    }
}