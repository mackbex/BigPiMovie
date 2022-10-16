package com.bigpi.movie.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigpi.movie.data.model.remote.MovieResponse
import com.bigpi.movie.data.model.remote.mapToDomain
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
//import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
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
//    private val updateBookmarkUseCase: UpdateBookmarkUseCase
) : ViewModel() {

    private val _bookmarkState = MutableSharedFlow<Resource<Movie>>()
    val bookmarkState: SharedFlow<Resource<Movie>> = _bookmarkState

    private val _movieState = MutableStateFlow<Movie?>(null)
    val movieState: StateFlow<Movie?> = _movieState


    init {
        _movieState.value = savedStateHandle.get<MovieResponse>("movie")?.mapToDomain()

    }

    fun updateBookmark(movie: Movie) {
        viewModelScope.launch {
//            _bookmarkState.emit(updateBookmarkUseCase.invoke(movie))
        }
    }

    fun updateMovieModel(movie: Movie) {
        _movieState.value = movie
    }
}