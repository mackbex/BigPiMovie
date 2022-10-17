package com.bigpi.movie.data.model.mapper

import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.model.remote.MovieListResponse
import com.bigpi.movie.data.model.remote.MovieResponse
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.model.remote.Movie

fun MovieResponse.mapToDomain(): MovieItem {
    return MovieItem(
        title = this.title,
        link = this.link,
        image = this.image,
        subtitle = this.subtitle,
        pubDate = this.pubDate,
        director = this.director,
        actor = this.actor,
        userRating = this.userRating,
        bookmark = this.bookmark
    )
}

fun MovieItem.mapToData(): MovieResponse {
    return MovieResponse(
        title = this.title,
        link = this.link,
        image = this.image,
        subtitle = this.subtitle,
        pubDate = this.pubDate,
        director = this.director,
        actor = this.actor,
        userRating = this.userRating,
        bookmark = this.bookmark
    )
}

fun MovieItem.mapToEntity(): MovieEntity {
    return MovieEntity(
        id = this.link ?: "",
        title = this.title,
        link = this.link,
        image = this.image,
        subtitle = this.subtitle,
        pubDate = this.pubDate,
        director = this.director,
        actor = this.actor,
        userRating = this.userRating,
        bookmark = this.bookmark
    )
}

fun MovieListResponse.mapToDomain(): Movie {
    return Movie(
        total = this.total,
        display = this.display,
        start = this.start,
        movieList = movieList.map { it.mapToDomain() }
    )
}