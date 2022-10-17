package com.bigpi.movie.domain.repository

import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.model.remote.MovieItem

interface MovieRepository {
    suspend fun fetchMovie(query: String, display: Int, start: Int): Resource<Movie>
    suspend fun addBookmark(movieItem: MovieItem): Resource<Long>
    suspend fun removeBookmark(movieItem: MovieItem): Resource<Int>
}