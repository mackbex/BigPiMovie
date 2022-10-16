package com.bigpi.movie.data.source.local

import androidx.paging.PagingSource
import com.bigpi.movie.data.getDeleteResult
import com.bigpi.movie.data.getInsertResult
import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.source.local.dao.MovieDao
import javax.inject.Inject


class MovieLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val movieDao: MovieDao,
){
    suspend fun getDb() = appDatabase

    suspend fun addMovie(bookmark: MovieEntity) = getInsertResult { movieDao.addMovie(bookmark) }
    
    suspend fun removeMovie(id: String) = getDeleteResult { movieDao.deleteMovieById(id) }

    suspend fun getMovieById(list: List<Int>): List<Int> {
        return movieDao.getMovieByIds(list)
    }

    fun getPagingSource(query: String): PagingSource<Int, MovieEntity> = movieDao.pagingSource(query)

    suspend fun insertAll(list: List<MovieEntity>) = movieDao.insertAll(list)

    suspend fun deleteAll() = movieDao.deleteAll()

}
