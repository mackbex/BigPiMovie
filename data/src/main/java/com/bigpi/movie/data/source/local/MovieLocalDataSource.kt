package com.bigpi.movie.data.source.local

import com.bigpi.movie.data.getDeleteResult
import com.bigpi.movie.data.getInsertResult
import com.bigpi.movie.data.model.local.MovieItemEntity
import com.bigpi.movie.data.api.local.MovieDao
import javax.inject.Inject


class MovieLocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
){
    suspend fun addBookmark(movieEntity: MovieItemEntity) = getInsertResult { movieDao.addBookmark(movieEntity) }
    
    suspend fun removeBookmark(movieEntity: MovieItemEntity) = getDeleteResult { movieDao.removeBookmark(movieEntity.id) }

    suspend fun getMovieById(list: List<String>): List<String> { return movieDao.getMovieByIds(list) }
}
