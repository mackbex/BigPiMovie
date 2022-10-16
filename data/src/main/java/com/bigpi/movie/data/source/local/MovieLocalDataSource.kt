package com.bigpi.movie.data.source.local

import androidx.paging.PagingSource
import com.bigpi.movie.data.getDeleteResult
import com.bigpi.movie.data.getInsertResult
import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.model.local.RemoteKeysEntity
import com.bigpi.movie.data.source.local.dao.MovieDao
import com.bigpi.movie.data.source.local.dao.RemoteKeysDao
import javax.inject.Inject


class MovieLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val movieDao: MovieDao,
    private val remoteKeysDao: RemoteKeysDao
){
    suspend fun getDb() = appDatabase

    suspend fun addMovie(bookmark: MovieEntity) = getInsertResult { movieDao.addMovie(bookmark) }
    
    suspend fun removeMovie(id: String) = getDeleteResult { movieDao.deleteMovieById(id) }

    suspend fun getMovieById(list: List<String>): List<String> {
        return movieDao.getMovieByIds(list)
    }

    fun getPagingSource(query: String): PagingSource<Int, MovieEntity> = movieDao.pagingSource("%${query.replace(' ', '%')}%")

    suspend fun insertAllMovies(list: List<MovieEntity>) = movieDao.insertAll(list)

    suspend fun clearMovies() = movieDao.clearMovies()

    suspend fun remoteKeysOfId(id: String) = remoteKeysDao.remoteKeysRepoId(id)

    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeysEntity>) = remoteKeysDao.insertAll(remoteKey)

    suspend fun clearRemoteKeys() = remoteKeysDao.clearRemoteKeys()
}
