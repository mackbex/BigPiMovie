package com.bigpi.movie.domain.repository

import androidx.paging.PagingData
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMovieList(query: String): Flow<PagingData<Movie>>
    suspend fun addBookmark(id: String): Resource<Long>
    suspend fun removeBookmark(id: String): Resource<Int>
}