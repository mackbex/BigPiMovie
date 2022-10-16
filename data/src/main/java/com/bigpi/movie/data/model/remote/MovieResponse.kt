package com.bigpi.movie.data.model.remote

import android.os.Parcelable
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.model.remote.MovieList
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MovieListResponse(
    @SerializedName("items") val movieList: List<MovieResponse> = listOf()
)

@Parcelize
data class MovieResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("pubDate") val pubDate: String? = null,
    @SerializedName("director") val director: String? = null,
    @SerializedName("actor") val actor: String? = null,
    @SerializedName("userRating") val userRating: String? = null,
    val bookmark: Boolean = false
) : Parcelable

fun MovieListResponse.mapToDomain(): MovieList {
    return MovieList(
        movieList = movieList.map { it.mapToDomain() }
    )
}

fun MovieResponse.mapToDomain(): Movie {
    return Movie(
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

fun Movie.mapToData(): MovieResponse {
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