//package com.bigpi.movie.data.source.remote
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.bigpi.movie.data.map
//import com.bigpi.movie.data.model.mapper.mapToDomain
//import com.bigpi.movie.data.source.local.MovieLocalDataSource
//import com.bigpi.movie.domain.Resource
//import com.bigpi.movie.domain.model.remote.Movie
//import java.io.IOException
//import javax.inject.Inject
//
//
//class MoviePagingSource @Inject constructor(
//    private val movieDataSource: MovieRemoteDataSource,
//    private val bookmarkDataSource: MovieLocalDataSource,
//    private val query: String
//) : PagingSource<Int, Movie>() {
//
//    companion object {
//        const val START_INDEX = 1
//        const val PAGE_SIZE = 50
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
//        val position = params.key ?: START_INDEX
//
//        return try {
//            val response = movieDataSource.fetchMovie(query, PAGE_SIZE.toString(), position.toString())
//            val movies = response.map {
//                it.mapToDomain()
//            }
//
//            var movieList = listOf<Movie>()
//            val nextKey = when (movies) {
//                is Resource.Success -> {
////                    movieList = checkBookmark(movies.data.movieList.toMutableList())
//                    movieList = movies.data.movieList
//
//                    if(movies.data.movieList.size >= PAGE_SIZE) {
//                        position + PAGE_SIZE
//                    } else {
//                        null
//                    }
//                }
//                is Resource.Failure -> {
//                    return LoadResult.Error(IOException(movies.apiFailure.msg))
//                }
//                is Resource.Loading -> {
//                    null
//                }
//            }
//
//            LoadResult.Page(
//                data = movieList,
//                prevKey = if(position == START_INDEX) null else position - PAGE_SIZE,
//                nextKey = nextKey
//            )
//
//        } catch (e: Exception) {
//            return LoadResult.Error(e)
//        }
//
//
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
//        return state.anchorPosition?.let { position ->
//            state.closestPageToPosition(position)?.prevKey?.plus(PAGE_SIZE)
//                ?: state.closestPageToPosition(position)?.nextKey?.minus(PAGE_SIZE)
//        }
//    }
//
////    private suspend fun checkBookmark(list:MutableList<Movie>): List<Movie> {
////        if(list.isEmpty()) return list
////        val bookmarkList = bookmarkDataSource.getMovieById(list.mapNotNull { it.link })
////        list.forEachIndexed { index, movie ->
////            if(bookmarkList.contains(movie.link)) {
////                list[index] = movie.copy(bookmark = true)
////            }
////        }
////
////        return list
////    }
//}