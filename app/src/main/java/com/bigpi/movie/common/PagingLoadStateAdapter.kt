package com.bigpi.movie.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bigpi.movie.R
import com.bigpi.movie.databinding.ItemNetworkPagingStateBinding


class PagingLoadStateAdapter<T:Any, HOLDER: RecyclerView.ViewHolder>(
    private val adapter: PagingDataAdapter<T, HOLDER>
): LoadStateAdapter<PagingLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder =
        ViewHolder(
            ItemNetworkPagingStateBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.item_network_paging_state, parent, false)
            )
        ){ adapter.retry() }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) = holder.bind(loadState)

    class ViewHolder(
        private val binding: ItemNetworkPagingStateBinding,
        private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.tvStateError.isVisible = loadState is LoadState.Error
            binding.progress.isVisible = loadState is LoadState.Loading

            binding.btnRetry.setOnClickListener {
                binding.progress.isVisible = true
                retryCallback.invoke()
            }
        }
    }
}