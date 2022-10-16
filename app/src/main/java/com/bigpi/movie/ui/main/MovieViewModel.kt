package com.bigpi.movie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.usecase.SearchMovieUseCase
import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
import com.bigpi.movie.ui.main.model.MoviePresentation
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

    private val _searchMovieState = MutableStateFlow<PagingData<MoviePresentation>>(PagingData.empty())
    val searchMovieState: StateFlow<PagingData<MoviePresentation>> = _searchMovieState

    private val _bookmarkState = MutableSharedFlow<Resource<Movie>>()
    val bookmarkState: SharedFlow<Resource<Movie>> = _bookmarkState

    val scrollResetRequired = AtomicBoolean(false)

    fun searchMovie(query: String) {
        viewModelScope.launch {
            searchMovieUseCase.invoke(query)
                .cachedIn(this)
                .collectLatest { pagingData ->

                    val mappedPagingData = addSeparator(pagingData)
                    _searchMovieState.value = mappedPagingData
                }
        }
    }

    fun updateBookmark(movie: Movie) {
        viewModelScope.launch {
            _bookmarkState.emit(updateBookmarkUseCase.invoke(movie))
        }
    }

    private fun addSeparator(pagingData: PagingData<Movie>): PagingData<MoviePresentation> {
        return pagingData
            .map<Movie, MoviePresentation> { data ->
                MoviePresentation.MovieItem(data)
            }
            .insertSeparators { before: MoviePresentation?, after: MoviePresentation? ->
                if(before == null) {
                    return@insertSeparators null
                }
                if (after == null) {
                    return@insertSeparators null
                }
                return@insertSeparators MoviePresentation.SeparatorItem
            }
    }
}