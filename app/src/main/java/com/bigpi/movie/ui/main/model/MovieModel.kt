package com.bigpi.movie.ui.main.model

import com.bigpi.movie.domain.model.remote.Movie


sealed class MovieModel(val ordinal: Int) {
    enum class Type {
        SeparatorItem, MovieItem
    }

    object SeparatorItem : MovieModel(Type.SeparatorItem.ordinal)
    data class MovieItem(val movie: Movie) : MovieModel(Type.MovieItem.ordinal)
}