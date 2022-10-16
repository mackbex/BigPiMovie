package com.bigpi.movie.domain.usecase

import com.bigpi.movie.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String) = movieRepository.getMovieList(query)
}