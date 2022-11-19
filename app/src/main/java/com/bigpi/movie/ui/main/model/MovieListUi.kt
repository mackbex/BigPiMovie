package com.bigpi.movie.ui.main.model

import android.os.Parcelable
import com.bigpi.movie.domain.model.remote.Movie
import kotlinx.parcelize.Parcelize


data class MovieUiState(
    val movie: Movie? = null,
    val hasMoreLoads: Boolean = movie?.let { (movie.total - (movie.display + movie.start) > 0)} ?: false,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

sealed class MovieListUi(val ordinal: Int) {
    enum class Type {
        PagingState, MovieItem
    }

    data class PagingStateUi(
        val state: PagingState = PagingState.Loading
    ) : MovieListUi(Type.PagingState.ordinal) {
        enum class PagingState {
            Loading, Success, Failure
        }
    }

    @Parcelize
    data class MovieItemUi(
        val title: String? = null,
        val link: String? = null,
        val image: String? = null,
        val subtitle: String? = null,
        val pubDate: String? = null,
        val director: String? = null,
        val actor: String? = null,
        val userRating: String? = null,
        val bookmark: Boolean = false
    ) : MovieListUi(Type.MovieItem.ordinal), Parcelable {
        fun copy(bookmark: Boolean) = MovieItemUi(title, link, image, subtitle, pubDate, director, actor, userRating, bookmark)
    }
}

