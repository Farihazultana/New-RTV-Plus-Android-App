package com.rtvplus.utils

sealed class ResultType<out T> {
    object Loading : ResultType<Nothing>()
    data class Success<out T>(val data: T) : ResultType<T>()
    data class Error(val exception: Throwable) : ResultType<Nothing>()
}