package com.harang.data.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val code: ErrorCode, val message: String = "") : Result<Nothing>()
}

enum class ErrorCode {
    DUPLICATE_ID,        // 10301
    INVALID_CREDENTIALS, // 10103
    NETWORK_ERROR,
    UNKNOWN,
}
