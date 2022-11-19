package com.bigpi.movie.data

import com.bigpi.movie.domain.model.remote.ApiFailure
import com.bigpi.movie.domain.Resource
import retrofit2.Response

inline fun <reified T: Any, reified R: Any> Resource<T>.map(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> try {
            Resource.Success(transform(data))
        } catch (e:Exception) {
            Resource.Failure(ApiFailure(msg = e.message))
        }
        is Resource.Failure -> Resource.Failure(this.apiFailure)
    }
}

suspend inline fun <T> getRemoteResult(crossinline call: suspend () -> Response<T>): Resource<T> {
    try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return Resource.Success(body)
        }
        return error(ApiFailure(msg = "Failed to load data."))
    } catch (e: Exception) {
        return error(ApiFailure(e.message ?: e.toString()))
    }
}

suspend inline fun getInsertResult(crossinline transaction: suspend () -> Long): Resource<Long> {
    try {
        val result = transaction()
        if (result > -1) {
            return Resource.Success(result)
        }
        return error(ApiFailure(msg = "Failed to insert data."))
    } catch (e: Exception) {
        return error(ApiFailure(e.message ?: e.toString()))
    }
}

suspend inline fun getDeleteResult(crossinline transaction: suspend () -> Int): Resource<Int> {
    try {
        val result = transaction()
        if (result > -1) {
            return Resource.Success(result)
        }
        return error(ApiFailure(msg = "Failed to remove data."))
    } catch (e: Exception) {
        return error(ApiFailure(e.message ?: e.toString()))
    }
}


fun <T> error(apiFailure: ApiFailure): Resource<T> {
    return Resource.Failure(apiFailure)
}