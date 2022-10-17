package com.bigpi.movie.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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
