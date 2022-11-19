package com.bigpi.movie.domain.usecase

import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.ApiFailure
import com.bigpi.movie.domain.model.remote.MovieItem
import com.bigpi.movie.domain.repository.MovieRepository
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movie: MovieItem): Resource<MovieItem> {
        return movie.link?.let { id ->
            return if(movie.bookmark) {
                when(val result = movieRepository.removeBookmark(movie)) {
                    is Resource.Success -> {
                        if(result.data > -1) {
                            Resource.Success(movie.copy(bookmark = false))
                        }
                        else {
                            Resource.Failure(ApiFailure("Bookmark status is not updated."))
                        }
                    }
                    is Resource.Failure -> {
                        result
                    }
                }

            }
            else {
                when(val result = movieRepository.addBookmark(movie)) {
                    is Resource.Success -> {
                        if(result.data > -1) {
                            Resource.Success(movie.copy(bookmark = true))
                        }
                        else {
                            Resource.Failure(ApiFailure("Bookmark status is not updated."))
                        }
                    }
                    is Resource.Failure -> {
                        result
                    }
                }
            }



        } ?: run {
            Resource.Failure(ApiFailure(msg = "Failed to archive movie id."))
        }
    }
}