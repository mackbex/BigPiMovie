package com.bigpi.movie.ui.main.model

import com.bigpi.movie.domain.model.remote.Movie


sealed class MoviePresentation(val ordinal: Int) {
    enum class Type {
        SeparatorItem, MovieItem
    }

    object SeparatorItem : MoviePresentation(Type.SeparatorItem.ordinal)
    data class MovieItem(val movie: Movie) : MoviePresentation(Type.MovieItem.ordinal)
}