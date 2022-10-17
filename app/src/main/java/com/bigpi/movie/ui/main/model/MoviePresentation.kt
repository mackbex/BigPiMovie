package com.bigpi.movie.ui.main.model

import android.os.Parcelable
import com.bigpi.movie.domain.Resource
import kotlinx.parcelize.Parcelize


sealed class MoviePresentation(val ordinal: Int) {
    enum class Type {
        PagingState, MovieItem
    }

    data class PagingStatePresent(
        val state: Resource<Nothing?>
    ) : MoviePresentation(Type.PagingState.ordinal)

    data class MoviePresent(
        val total: Long,
        val display: Int,
        val start: Int,
        val hasMoreLoads: Boolean = (total - (display + start) > 0),
        val movieList: List<MovieItemPresent> = listOf()
    )  {
        fun copy(movieList: List<MovieItemPresent>) = MoviePresent(total, display, start, hasMoreLoads, movieList)
    }

    @Parcelize
    data class MovieItemPresent(
        val title: String? = null,
        val link: String? = null,
        val image: String? = null,
        val subtitle: String? = null,
        val pubDate: String? = null,
        val director: String? = null,
        val actor: String? = null,
        val userRating: String? = null,
        val bookmark: Boolean = false
    ) : MoviePresentation(Type.MovieItem.ordinal), Parcelable {
        fun copy(bookmark: Boolean) = MovieItemPresent(title, link, image, subtitle, pubDate, director, actor, userRating, bookmark)
    }
}

