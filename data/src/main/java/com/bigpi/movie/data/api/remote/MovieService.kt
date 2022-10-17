package com.bigpi.movie.data.api.remote

import com.bigpi.movie.data.model.remote.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("search/movie.json")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("display") display: Int,
        @Query("start") start: Int,

    ): Response<MovieListResponse>
}