package com.bigpi.movie.domain.model.remote

data class Movie(
    val total: Long,
    val display: Int,
    val start: Int,
    val hasMoreLoads: Boolean = (total - (display + start) > 0),
    val movieList: List<MovieItem> = listOf()
) {
    fun copy(movieList: List<MovieItem>) = Movie(total, display, start, hasMoreLoads, movieList)
}

data class MovieItem(
    val title: String? = null,
    val link: String? = null,
    val image: String? = null,
    val subtitle: String? = null,
    val pubDate: String? = null,
    val director: String? = null,
    val actor: String? = null,
    val userRating: String? = null,
    val bookmark: Boolean = false
) {
    fun copy(bookmark: Boolean) = MovieItem(title, link, image, subtitle, pubDate, director, actor, userRating, bookmark)
}

