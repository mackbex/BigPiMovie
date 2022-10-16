package com.bigpi.movie.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bigpi.movie.domain.model.remote.Movie

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey
    val id: String,
    val title: String? = null,
    val link: String? = null,
    val image: String? = null,
    val subtitle: String? = null,
    val pubDate: String? = null,
    val director: String? = null,
    val actor: String? = null,
    val userRating: String? = null,
    val bookmark: Boolean = false
)

fun MovieEntity.mapToDomain(): Movie {
    return Movie(
        title, link, image, subtitle, pubDate, director, actor, userRating
    )
}