package com.bigpi.movie.data.model.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MovieResponse(
    @SerializedName("total") val total: Long,
    @SerializedName("start") val start: Int,
    @SerializedName("display") val display: Int,
    @SerializedName("items") val movieList: List<MovieItemResponse> = listOf()
)

@Parcelize
data class MovieItemResponse(
    val id: Int? = null,
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

