package com.bigpi.movie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.usecase.SearchMovieUseCase
import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@HiltViewModel
class MovieViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase
) : ViewModel() {

    private val _searchMovieState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val searchMovieState: StateFlow<PagingData<Movie>> = _searchMovieState

    private val _bookmarkState = MutableSharedFlow<Resource<Movie>>()
    val bookmarkState: SharedFlow<Resource<Movie>> = _bookmarkState

    val scrollResetRequired = AtomicBoolean(false)

    fun searchMovie(query: String) {
        viewModelScope.launch {
            searchMovieUseCase.invoke(query)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _searchMovieState.value = pagingData
                }
        }
    }

    fun updateBookmark(movie: Movie) {
        viewModelScope.launch {
            _bookmarkState.emit(updateBookmarkUseCase.invoke(movie))
        }
    }
}