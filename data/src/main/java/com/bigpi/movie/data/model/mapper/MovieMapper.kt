package com.bigpi.movie.data.model.mapper

import com.bigpi.movie.data.model.local.MovieItemEntity
import com.bigpi.movie.data.model.remote.MovieItemResponse
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.model.remote.Movie

fun MovieItemResponse.mapToDomain(): MovieItem {
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

fun MovieItem.mapToData(): MovieItemResponse {
    return MovieItemResponse(
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

fun MovieItem.mapToEntity(): MovieItemEntity {
    return MovieItemEntity(
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

fun com.bigpi.movie.data.model.remote.MovieResponse.mapToDomain(): Movie {
    return Movie(
        total = this.total,
        display = this.display,
        start = this.start,
        movieList = movieList.map { it.mapToDomain() }
    )
}