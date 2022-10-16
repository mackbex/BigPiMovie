package com.bigpi.movie.module

import com.bigpi.movie.data.repository.remote.MovieRepositoryImpl
import com.bigpi.movie.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindRankRepository(impl: MovieRepositoryImpl): MovieRepository
}

