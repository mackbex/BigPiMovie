package com.bigpi.movie.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bigpi.movie.R
import com.bigpi.movie.data.model.mapper.mapToData
import com.bigpi.movie.databinding.FragmentMovieBinding
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.ui.BigPiConstants
import com.bigpi.movie.ui.detail.DetailFragmentArgs
import com.bigpi.movie.ui.main.model.MovieListUi
import com.bigpi.movie.ui.main.model.mapper.mapToPresent
import com.bigpi.movie.util.InfiniteScrollListener
import com.bigpi.movie.util.autoCleared
import com.bigpi.movie.util.hideSoftInput
import com.bigpi.movie.util.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private var binding: FragmentMovieBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()
    val movieListAdapter: MovieListAdapter by lazy { MovieListAdapter() }

    companion object {
        const val START_INDEX = 1
        const val PAGE_SIZE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieBinding.inflate(inflater, container, false)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MovieFragment.viewModel
            fragment = this@MovieFragment
            adapter = movieListAdapter.apply {
                setMovieItemAdapterListener { movieItem, binding ->
                    binding.imgBookmark.setOnClickListener {
                        this@MovieFragment.viewModel.updateBookmark(movieItem)
                    }

                    binding.root.setOnClickListener {
                        moveToDetail(movieItem)
                    }
                }
                setRetryListener {
                    movieScrollListener.retry()
                }
            }
            rcMovies.apply {
                itemAnimator = null
                addOnScrollListener(movieScrollListener)
            }
        }

        initObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(BigPiConstants.KEY_DETAIL_MOVIE) { _, result ->
            result.getParcelable<MovieListUi.MovieItemUi>(BigPiConstants.KEY_MOVIE_ITEM)?.let {
                movieListAdapter.updateBookmarkItem(it)
            }
        }
    }

    private fun initObservers() {
        viewModel.searchMovieState.launchAndCollectIn(viewLifecycleOwner) { state ->

            if(!state.isLoading) {
                state.movie?.let { movie ->
                    movieListAdapter.submitMovieItems(movie.movieItemList.map {
                        it.mapToPresent()
                    }, movie.hasMoreLoads)
                }

                state.userMessage?.let { msg ->
                    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    viewModel.userMessageShown()
                }
            }

            movieListAdapter.setLoadingState(state)
        }

        viewModel.bookmarkState.launchAndCollectIn(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    movieListAdapter.updateBookmarkItem(it.data.mapToPresent())
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.apiFailure.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val actionMovieSearch = TextView.OnEditorActionListener { _, actionId, _ ->
        if(actionId == EditorInfo.IME_ACTION_SEARCH) {
            val query = binding.etSearch.text?.toString()

            if(query.isNullOrEmpty()) {
                Toast.makeText(requireContext(), R.string.err_input_data, Toast.LENGTH_SHORT).show()
            }
            else {
                resetList()
                viewModel.fetchMovie(query, PAGE_SIZE, START_INDEX)
                binding.etSearch.hideSoftInput()
            }
        }
        return@OnEditorActionListener false
    }

    private fun resetList() {
        movieListAdapter.reset()
        movieScrollListener.reset()
    }

    val movieScrollListener = object : InfiniteScrollListener(START_INDEX, PAGE_SIZE) {
        override fun onLoadMore(start: Int) {
            this@MovieFragment.viewModel.run {
                fetchMovie(query, PAGE_SIZE, start)
            }
        }
    }


    private fun moveToDetail(movie: MovieItem) {
        findNavController().navigate(R.id.toDetailFragment,
            DetailFragmentArgs(
                movie = movie.mapToData()
            ).toBundle()
        )
    }

}