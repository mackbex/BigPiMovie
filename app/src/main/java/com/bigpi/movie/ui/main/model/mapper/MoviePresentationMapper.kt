package com.bigpi.movie.ui.main.model.mapper

import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.ui.main.model.MovieListUi


//fun Movie.mapToPresent() = MoviePresentation.MovieUiState(
//    total, display, start, hasMoreLoads, movieList)

fun MovieItem.mapToPresent() = MovieListUi.MovieItemUi(
    title, link, image, subtitle, pubDate, director, actor, userRating, bookmark
)

fun MovieListUi.MovieItemUi.mapToDomain() = MovieItem(
    title, link, image, subtitle, pubDate, director, actor, userRating, bookmark
)