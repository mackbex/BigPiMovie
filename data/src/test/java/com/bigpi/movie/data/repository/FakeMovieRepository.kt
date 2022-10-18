package com.bigpi.movie.data.repository

import com.bigpi.movie.data.factory.MovieFactory
import com.bigpi.movie.data.model.mapper.mapToDomain
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.repository.MovieRepository

class FakeMovieRepository(private val total: Long): MovieRepository {
    override suspend fun addBookmark(movieItem: MovieItem): Resource<Long> {
        return Resource.Success(0)
    }

    override suspend fun removeBookmark(movieItem: MovieItem): Resource<Int> {
        return Resource.Success(0)
    }

    override suspend fun fetchMovie(query: String, display: Int, start: Int): Resource<Movie> {
        return Resource.Success(MovieFactory().getMovieResponse(total, display, start).mapToDomain())
    }
}