package com.bigpi.movie.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bigpi.movie.databinding.FragmentDetailBinding
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var binding: FragmentDetailBinding by autoCleared()
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DetailFragment.viewModel
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initObservers()

        return binding.root
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
}