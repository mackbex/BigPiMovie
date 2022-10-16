package com.bigpi.movie.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.model.local.RemoteKeysEntity
import com.bigpi.movie.data.module.DATABASE_NAME
import com.bigpi.movie.data.source.local.dao.MovieDao
import com.bigpi.movie.data.source.local.dao.RemoteKeysDao

@Database(entities = [MovieEntity::class, RemoteKeysEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object{

        @Volatile
        private var instance : AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}