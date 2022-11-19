package com.bigpi.movie.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bigpi.movie.databinding.FragmentDetailBinding
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.ui.BigPiConstants
import com.bigpi.movie.ui.main.model.mapper.mapToPresent
import com.bigpi.movie.util.autoCleared
import com.bigpi.movie.util.setOnBackPressHandler
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var binding: FragmentDetailBinding by autoCleared()
    private val viewModel: DetailViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setOnBackPressHandler {
            onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DetailFragment.viewModel
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        initObservers()

        return binding.root
    }

    private fun onBackPressed() {
        setFragmentResult(
            BigPiConstants.KEY_DETAIL_MOVIE,
            bundleOf(BigPiConstants.KEY_MOVIE_ITEM to this@DetailFragment.viewModel.movieState.value?.mapToPresent())
        )
        findNavController().navigateUp()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookmarkState.collect {
                        when(it) {
                            is Resource.Success -> {
                                viewModel.updateMovieModel(it.data)
                            }
                            is Resource.Failure -> {
                                Toast.makeText(requireContext(), it.apiFailure.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}