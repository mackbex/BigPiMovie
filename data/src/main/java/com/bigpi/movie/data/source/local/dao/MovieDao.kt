package com.bigpi.movie.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bigpi.movie.data.model.local.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT id FROM movie WHERE id IN (:ids)")
    suspend fun getMovieByIds(ids: List<Int>): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: MovieEntity): Long

    @Query("DELETE FROM movie WHERE id = :id")
    suspend fun deleteMovieById(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list : List<MovieEntity>)

    @Query("DELETE FROM movie")
    fun deleteAll()

    @Query("SELECT * FROM movie WHERE id LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, MovieEntity>
}