package com.bigpi.movie.util.conversion

import androidx.databinding.BindingConversion
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.model.remote.Movie


@BindingConversion
fun convertResourceToMovie(
    resource: Resource<List<Movie>>
): List<Movie>? {
    return if(resource is Resource.Success) {
        resource.data
    }
    else {
        null
    }
}
