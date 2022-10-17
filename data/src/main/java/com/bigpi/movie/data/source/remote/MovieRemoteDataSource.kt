package com.bigpi.movie.data.source.remote

import com.bigpi.movie.data.api.remote.MovieService
import com.bigpi.movie.data.getRemoteResult
import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val movieService: MovieService
)  {
    suspend fun fetchMovie(
        query: String,
        display: Int,
        start: Int
    ) = getRemoteResult { movieService.searchMovie(query, display, start) }
}