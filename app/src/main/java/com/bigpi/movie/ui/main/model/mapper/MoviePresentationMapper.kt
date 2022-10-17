package com.bigpi.movie.ui.main.model.mapper

import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.ui.main.model.MoviePresentation


fun Movie.mapToPresent() = MoviePresentation.MoviePresent(
    total, display, start, hasMoreLoads, movieList.map {
        it.mapToPresent()
    }
)

fun MovieItem.mapToPresent() = MoviePresentation.MovieItemPresent(
    title, link, image, subtitle, pubDate, director, actor, userRating, bookmark
)

fun MoviePresentation.MovieItemPresent.mapToDomain() = MovieItem(
    title, link, image, subtitle, pubDate, director, actor, userRating, bookmark
)