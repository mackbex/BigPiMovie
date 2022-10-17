package com.bigpi.movie.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bigpi.movie.data.model.local.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT id FROM movie WHERE id IN (:ids)")
    suspend fun getMovieByIds(ids: List<String>): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(movieEntity: MovieEntity): Long

    @Query("DELETE FROM movie WHERE id = :id")
    suspend fun removeBookmark(id: String): Int

}