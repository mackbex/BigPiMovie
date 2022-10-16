package com.bigpi.movie.data.source.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bigpi.movie.data.model.local.MovieEntity
import com.bigpi.movie.data.model.local.RemoteKeysEntity
import com.bigpi.movie.data.model.remote.mapToEntity
import com.bigpi.movie.data.source.remote.MovieRemoteDataSource
import com.bigpi.movie.domain.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class MovieMediator @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val remoteDataSource: MovieRemoteDataSource,
    private val query: String
) : RemoteMediator<Int, MovieEntity>() {

    companion object {
        const val START_INDEX = 1
        const val PAGE_SIZE = 5
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(PAGE_SIZE) ?: START_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }


        try {
            when(val response = remoteDataSource.searchMovie(query,  PAGE_SIZE, page)) {
                is Resource.Success -> {
                    val endOfPaginationReached = response.data.movieList.size < PAGE_SIZE

                    localDataSource.getDb().withTransaction {
                        // clear all tables in the database
                        if (loadType == LoadType.REFRESH) {
                            localDataSource.clearRemoteKeys()
                            localDataSource.clearMovies()
                        }
                        val prevKey = if (page == START_INDEX) null else page
                        val nextKey = if (endOfPaginationReached) null else page + PAGE_SIZE
                        val keys = response.data.movieList.map {
                            RemoteKeysEntity(id = it.link ?: "", prevKey = prevKey, nextKey = nextKey)
                        }
                        localDataSource.insertAllRemoteKeys(keys)
                        localDataSource.insertAllMovies(response.data.movieList.map {
                            it.copy(id = it.link).mapToEntity()
                        })
                    }

                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is Resource.Loading -> {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
                is Resource.Failure -> {
                    return MediatorResult.Error(IOException(response.apiFailure.msg))
                }
            }
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MovieEntity>): RemoteKeysEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                localDataSource.remoteKeysOfId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieEntity>): RemoteKeysEntity? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // Get the remote keys of the first items retrieved
                localDataSource.remoteKeysOfId(movie.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeysEntity? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                localDataSource.remoteKeysOfId(repoId)
            }
        }
    }

//
//    private suspend fun refresh(): MediatorResult {
//        try {
//            when(val list = remoteDataSource.searchMovie(query, PAGE_SIZE.toString(), "1")) {
//                is Resource.Success -> {
//                    val movieList = list.data.movieList
//                    if (movieList.isNotEmpty()) {
//                        localDataSource.getDb().withTransaction {
//                            localDataSource.deleteAll()
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
//
//        } catch (e: Exception) {
//            return MediatorResult.Error(e)
//        }
//        return MediatorResult.Success(endOfPaginationReached = true)
//    }
//
//    private suspend fun loadBefore(state: PagingState<Int, MovieEntity>): MediatorResult {
//        return MediatorResult.Success(endOfPaginationReached = true)
//    }
//
//    private suspend fun loadAfter(state: PagingState<Int, MovieEntity>): MediatorResult {
//        return MediatorResult.Success(endOfPaginationReached = true)
////        val lastItem = getKey(state)
////            ?: return MediatorResult.Success(
////                endOfPaginationReached = true
////            )
////
////        try {
////
////            when(val list = remoteDataSource.searchMovie(query, PAGE_SIZE.toString(), lastItem.toString())) {
////                is Resource.Success -> {
////                    val movieList = list.data.movieList
////                    if (movieList.isNotEmpty()) {
////                        localDataSource.getDb().withTransaction {
////                            localDataSource.insertAll(movieList.map {
////                                it.mapToEntity()
////                            })
////                        }
////                    }
////                }
////                is Resource.Loading -> {}
////                is Resource.Failure -> {
////                    MediatorResult.Error(IOException(list.apiFailure.msg))
////                }
////            }
////            state.lastItemOrNull()
////
////            return MediatorResult.Success(endOfPaginationReached = state.lastItemOrNull()?.let { false } ?: true)
////        } catch (e: Exception) {
////            return MediatorResult.Error(e)
////        }
//    }
//
//    fun getKey(state: PagingState<Int, MovieEntity>): Int? {
//        return state.anchorPosition?.let { position ->
//            state.closestPageToPosition(position)?.prevKey?.plus(MoviePagingSource.PAGE_SIZE)
//                ?: state.closestPageToPosition(position)?.nextKey?.minus(MoviePagingSource.PAGE_SIZE)
//        }
//    }


}