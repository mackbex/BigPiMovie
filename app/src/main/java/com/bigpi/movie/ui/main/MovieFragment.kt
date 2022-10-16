package com.bigpi.movie.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import com.bigpi.movie.R
import com.bigpi.movie.data.model.remote.mapToData
import com.bigpi.movie.databinding.FragmentMovieBinding
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.ext.loadFooter
import com.bigpi.movie.ui.detail.DetailFragmentArgs
import com.bigpi.movie.util.autoCleared
import com.bigpi.movie.util.hideSoftInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private var binding: FragmentMovieBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()
    val movieListAdapter: MovieListAdapter by lazy { MovieListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieBinding.inflate(inflater, container, false)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MovieFragment.viewModel
            fragment = this@MovieFragment
            etSearch.apply {
                setOnKeyListener(searchOnKeyListener)
                setOnEditorActionListener(searchActionListener)
            }
            rcMovies.apply {
                adapter = getPagingAdapter()
            }
        }

        initObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieListAdapter.refresh()
    }


    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchMovieState.collect { pagingData ->
                        movieListAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                    }
                }
                launch {
                    viewModel.bookmarkState.collect {
                        when(it) {
                            is Resource.Success -> {
                                movieListAdapter.refresh()
                            }
                            is Resource.Loading -> {}
                            is Resource.Failure -> {
                                Toast.makeText(requireContext(), it.apiFailure.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPagingAdapter(): ConcatAdapter {
        return movieListAdapter.apply {

            setAdapterListeners { movie, binding ->
                binding.imgBookmark.setOnClickListener {
                    viewModel.updateBookmark(movie)
                }

                binding.root.setOnClickListener {
                    moveToDetail(movie)
                }
            }

            addLoadStateListener { states ->
                if(states.refresh is LoadState.Error) {
                    Toast.makeText(requireContext(), R.string.err_failed_load_data, Toast.LENGTH_SHORT).show()
                }
            }

            lifecycleScope.launch {
                loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect {
                        if(viewModel.scrollResetRequired.getAndSet(false)) {
                            binding.rcMovies.scrollToPosition(0)
                        }
                    }
            }
        }.loadFooter()
    }

    val searchActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if(actionId == EditorInfo.IME_ACTION_SEARCH) {
            search()
            true
        }
        else {
            false
        }
    }

    val searchOnKeyListener = View.OnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            search()
            true
        } else {
            false
        }
    }

    private fun search() {
        binding.etSearch.text?.toString()?.let { query ->
            viewModel.scrollResetRequired.set(true)
            this@MovieFragment.viewModel.searchMovie(query)
            binding.etSearch.hideSoftInput()
        } ?: run {
            Toast.makeText(requireContext(), R.string.err_input_data, Toast.LENGTH_SHORT).show()
        }
    }


    private fun moveToDetail(movie: Movie) {
        findNavController().navigate(R.id.toDetailFragment,
            DetailFragmentArgs(
                movie = movie.mapToData()
            ).toBundle()
        )
    }

}