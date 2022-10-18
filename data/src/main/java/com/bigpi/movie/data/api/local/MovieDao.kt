package com.bigpi.movie.data.api.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bigpi.movie.data.model.local.MovieItemEntity

@Dao
interface MovieDao {

    @Query("SELECT id FROM movie_item WHERE id IN (:ids)")
    suspend fun getMovieByIds(ids: List<String>): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(movieEntity: MovieItemEntity): Long

    @Query("DELETE FROM movie_item WHERE id = :id")
    suspend fun removeBookmark(id: String): Int

}