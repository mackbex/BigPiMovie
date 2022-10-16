package com.bigpi.movie.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bigpi.movie.data.model.local.BookmarkEntity

@Dao
interface BookmarkDao {

    @Query("SELECT id FROM bookmark WHERE id IN (:ids)")
    suspend fun getBookmarkByIds(ids: List<String>): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmark WHERE id = :id")
    suspend fun deleteBookmarkById(id: String): Int
}