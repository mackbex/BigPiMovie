package com.bigpi.movie.data.source.local

import com.bigpi.movie.data.getDeleteResult
import com.bigpi.movie.data.getInsertResult
import com.bigpi.movie.data.getRemoteResult
import com.bigpi.movie.data.model.local.BookmarkEntity
import com.bigpi.movie.data.source.local.dao.BookmarkDao
import javax.inject.Inject


class BookmarkDataSource @Inject constructor(
    private val bookmarkDao: BookmarkDao,
){
    suspend fun addBookmark(bookmark: BookmarkEntity) = getInsertResult { bookmarkDao.addBookmark(bookmark) }
    
    suspend fun removeBookmark(id: String) = getDeleteResult { bookmarkDao.deleteBookmarkById(id) }

    suspend fun getBookmarksById(list: List<String>): List<String> {
        return bookmarkDao.getBookmarkByIds(list)
    }
}
