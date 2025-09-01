package com.ariabagas.storiaapp.utils

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T, val message: String? = null) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}