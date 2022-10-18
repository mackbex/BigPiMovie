package com.bigpi.movie.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigpi.movie.data.model.remote.MovieItemResponse
import com.bigpi.movie.data.model.mapper.mapToDomain
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase
) : ViewModel() {

    private val _bookmarkState = MutableSharedFlow<Resource<MovieItem>>()
    val bookmarkState: SharedFlow<Resource<MovieItem>> = _bookmarkState

    private val _movieState = MutableStateFlow<MovieItem?>(null)
    val movieState: StateFlow<MovieItem?> = _movieState


    init {
        _movieState.value = savedStateHandle.get<MovieItemResponse>("movie")?.mapToDomain()

    }

    fun updateBookmark(movie: MovieItem) {
        viewModelScope.launch {
            _bookmarkState.emit(updateBookmarkUseCase.invoke(movie))
        }
    }

    fun updateMovieModel(movie: MovieItem) {
        _movieState.value = movie
    }
}