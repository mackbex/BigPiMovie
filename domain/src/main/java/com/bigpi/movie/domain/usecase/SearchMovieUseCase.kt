package com.bigpi.movie.domain.usecase

import com.bigpi.movie.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String, display: Int, start: Int) = movieRepository.fetchMovie(query, display, start)
}