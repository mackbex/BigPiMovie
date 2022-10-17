package com.bigpi.movie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigpi.movie.data.map
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.usecase.SearchMovieUseCase
import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
import com.bigpi.movie.ui.main.model.MoviePresentation
import com.bigpi.movie.ui.main.model.mapper.mapToPresent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase
) : ViewModel() {

    private val _searchMovieState = MutableSharedFlow<Resource<MoviePresentation.MoviePresent>>()
    val searchMovieState: SharedFlow<Resource<MoviePresentation.MoviePresent>> = _searchMovieState

    private val _bookmarkState = MutableSharedFlow<Resource<MovieItem>>()
    val bookmarkState: SharedFlow<Resource<MovieItem>> = _bookmarkState

    var query = ""

    fun fetchMovie(query: String, display: Int, start: Int) {
        this.query = query
        viewModelScope.launch {
            _searchMovieState.emit(
                searchMovieUseCase.invoke(query, display, start)
                .map { it.mapToPresent() }
            )
        }
    }

    fun updateBookmark(movie: MovieItem) {
        viewModelScope.launch {
            _bookmarkState.emit(updateBookmarkUseCase.invoke(movie))
        }
    }

}