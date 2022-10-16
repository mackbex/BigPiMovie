package com.bigpi.movie.data.module

import android.content.Context
import com.bigpi.movie.data.source.local.AppDatabase
import com.bigpi.movie.data.source.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room database 모듈
 */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.bookmarkDao()
    }
}