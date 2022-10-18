package com.bigpi.movie.data.factory

import com.bigpi.movie.data.model.remote.MovieResponse

class MovieFactory {

    fun getMovieResponse(total: Long, display: Int, start: Int): MovieResponse {
        return MovieResponse(
            total = total,
            display = display,
            start = start
        )
    }
}