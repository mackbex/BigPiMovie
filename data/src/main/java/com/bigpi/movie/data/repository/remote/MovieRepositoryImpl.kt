package com.bigpi.movie.data.repository.remote

import androidx.paging.*
import com.bigpi.movie.data.model.local.mapToDomain
import com.bigpi.movie.data.source.local.MovieLocalDataSource
import com.bigpi.movie.data.source.local.MovieMediator
import com.bigpi.movie.data.source.local.MovieMediator.Companion.PAGE_SIZE
import com.bigpi.movie.data.source.remote.MovieRemoteDataSource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
): MovieRepository {

    override suspend fun getMovieList(query: String): Flow<PagingData<Movie>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = MovieMediator(
                remoteDataSource = movieRemoteDataSource,
                localDataSource = movieLocalDataSource,
                query = query)
        ) {
            movieLocalDataSource.getPagingSource(query)
        }.flow
            .map { pagingData ->
                pagingData.map { it.mapToDomain() }
            }
    }

//    override suspend fun addBookmark(id: String): Resource<Long> {
//        return bookmarkDataSource.addBookmark(BookmarkEntity(
//            id = id
//        ))
//    }
//
//    override suspend fun removeBookmark(id: String): Resource<Int> {
//        return bookmarkDataSource.removeBookmark(id)
//    }
}