package com.bigpi.movie.data.repository.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bigpi.movie.data.model.local.BookmarkEntity
import com.bigpi.movie.data.source.local.BookmarkDataSource
import com.bigpi.movie.data.source.remote.MovieDataSource
import com.bigpi.movie.data.source.remote.MoviePagingSource
import com.bigpi.movie.data.source.remote.MoviePagingSource.Companion.PAGE_SIZE
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import com.bigpi.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val bookmarkDataSource: BookmarkDataSource
): MovieRepository {

    override suspend fun getMovieList(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(movieDataSource, bookmarkDataSource, query) }
        ).flow
    }

    override suspend fun addBookmark(id: String): Resource<Long> {
        return bookmarkDataSource.addBookmark(BookmarkEntity(
            id = id
        ))
    }

    override suspend fun removeBookmark(id: String): Resource<Int> {
        return bookmarkDataSource.removeBookmark(id)
    }
}