package com.bigpi.movie.data.api.remote

import com.bigpi.movie.data.model.remote.MovieListResponse
import com.bigpi.movie.data.source.remote.MoviePagingSource.Companion.PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("search/movie.json")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("display") display: String = PAGE_SIZE.toString(),
        @Query("start") start: String,

    ): Response<MovieListResponse>
}