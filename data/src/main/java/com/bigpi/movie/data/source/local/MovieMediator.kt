package com.bigpi.movie.data.source.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.model.remote.mapToEntity
import com.bigpi.movie.data.source.PAGE_SIZE
import com.bigpi.movie.data.source.START_INDEX
import com.bigpi.movie.data.source.remote.MoviePagingSource
import com.bigpi.movie.data.source.remote.MovieRemoteDataSource
import com.bigpi.movie.domain.Resource
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class MovieMediator @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val remoteDataSource: MovieRemoteDataSource,
    private val query: String
) : RemoteMediator<Int, MovieEntity>() {


    private var lastPosition = START_INDEX

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {
        return when (loadType) {
            LoadType.REFRESH -> refresh()
            LoadType.PREPEND -> loadAfter(state)
            LoadType.APPEND -> loadBefore(state)
        }
    }

    private suspend fun refresh(): MediatorResult {
        try {
            when(val list = remoteDataSource.searchMovie(query, PAGE_SIZE.toString(), "1")) {
                is Resource.Success -> {
                    val movieList = list.data.movieList
                    if (movieList.isNotEmpty()) {
                        localDataSource.getDb().withTransaction {
                            localDataSource.deleteAll()
                            localDataSource.insertAll(movieList.map {
                                it.mapToEntity()
                            })
                        }
                    }
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    MediatorResult.Error(IOException(list.apiFailure.msg))
                }
            }

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
        return MediatorResult.Success(endOfPaginationReached = true)
    }

    private suspend fun loadBefore(state: PagingState<Int, MovieEntity>): MediatorResult {
        return MediatorResult.Success(endOfPaginationReached = true)
    }

    private suspend fun loadAfter(state: PagingState<Int, MovieEntity>): MediatorResult {
        return MediatorResult.Success(endOfPaginationReached = true)
//        val lastItem = getKey(state)
//            ?: return MediatorResult.Success(
//                endOfPaginationReached = true
//            )
//
//        try {
//
//            when(val list = remoteDataSource.searchMovie(query, PAGE_SIZE.toString(), lastItem.toString())) {
//                is Resource.Success -> {
//                    val movieList = list.data.movieList
//                    if (movieList.isNotEmpty()) {
//                        localDataSource.getDb().withTransaction {
//                            localDataSource.insertAll(movieList.map {
//                                it.mapToEntity()
//                            })
//                        }
//                    }
//                }
//                is Resource.Loading -> {}
//                is Resource.Failure -> {
//                    MediatorResult.Error(IOException(list.apiFailure.msg))
//                }
//            }
//            state.lastItemOrNull()
//
//            return MediatorResult.Success(endOfPaginationReached = state.lastItemOrNull()?.let { false } ?: true)
//        } catch (e: Exception) {
//            return MediatorResult.Error(e)
//        }
    }

    fun getKey(state: PagingState<Int, MovieEntity>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(MoviePagingSource.PAGE_SIZE)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(MoviePagingSource.PAGE_SIZE)
        }
    }


}