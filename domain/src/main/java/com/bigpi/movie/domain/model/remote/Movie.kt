package com.bigpi.movie.domain.model.remote

data class MovieList(
    val movieList: List<Movie> = listOf()
)

data class Movie(
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
    fun copy(bookmark: Boolean) = Movie(title, link, image, subtitle, pubDate, director, actor, userRating, bookmark)
}

