package io.github.maybeinotherlife.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val error: String) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()

    val stringState: String
        get() = when (this) {
            is Loading -> "loading"
            is Success -> "success"
            is Error -> "error"
        }
}