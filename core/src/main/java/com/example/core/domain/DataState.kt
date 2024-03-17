package com.example.core.domain

sealed class DataState<T>(
    var data: T? = null,
    var apiError: ApiError? = null,
) {
    class Loading<T> : DataState<T>()

    class Success<T>(
        data: T? = null,
        var cached: Boolean = false
    ) : DataState<T>(
        data = data
    )

    class Error<T>(
        apiError: ApiError? = null,
        data: T? = null
    ) : DataState<T>(
        data = data,
        apiError = apiError
    )
}
