package com.bigpi.movie.data.source.remote

import com.bigpi.movie.data.api.remote.MovieService
import com.bigpi.movie.data.getRemoteResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val movieService: MovieService
)  {
    suspend fun searchMovie(
        query: String,
        display: String,
        start: String
    ) = getRemoteResult { movieService.searchMovie(query, display, start) }
}