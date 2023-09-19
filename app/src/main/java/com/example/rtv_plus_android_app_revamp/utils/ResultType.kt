package com.example.rtv_plus_android_app_revamp.utils

sealed class ResultType<out T> {
    object Loading : ResultType<Nothing>()
    data class Success<out T>(val data: T) : ResultType<T>()
    data class Error(val exception: Throwable) : ResultType<Nothing>()
}