package com.bigpi.movie.data.repository.remote

import com.bigpi.movie.data.map
import com.bigpi.movie.data.model.mapper.mapToDomain
import com.bigpi.movie.data.model.mapper.mapToEntity
import com.bigpi.movie.data.source.local.MovieLocalDataSource
import com.bigpi.movie.data.source.remote.MovieRemoteDataSource
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun fetchMovie(
        query: String,
        display: Int,
        start: Int
    ): Resource<Movie> {
        return movieRemoteDataSource.fetchMovie(query, display, start).map { movieResponse ->
            movieResponse.mapToDomain()
                .copy(
                    checkBookmark(movieResponse
                        .movieList
                        .map {
                            it.mapToDomain()
                        }
                        .toMutableList()
                ))
        }
    }

    private suspend fun checkBookmark(list: MutableList<MovieItem>): List<MovieItem> {
        if (list.isEmpty()) return list
        val bookmarkList = movieLocalDataSource.getMovieById(list.mapNotNull { it.link })
        list.forEachIndexed { index, movie ->
            if (bookmarkList.contains(movie.link)) {
                list[index] = movie.copy(bookmark = true)
            }
        }

        return list
    }

    override suspend fun addBookmark(movieItem: MovieItem): Resource<Long> {
        return movieLocalDataSource.addBookmark(movieItem.mapToEntity())
    }

    override suspend fun removeBookmark(movieItem: MovieItem): Resource<Int> {
        return movieLocalDataSource.removeBookmark(movieItem.mapToEntity())
    }
}