package com.ariabagas.storiaapp.utils

import org.json.JSONObject
import retrofit2.HttpException

object ApiErrorHandler {
    fun getErrorMessage(e: Throwable): String {
        return if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            try {
                JSONObject(errorBody ?: "").getString("message")
            } catch (ex: Exception) {
                e.message() ?: "Unknown error"
            }
        } else {
            e.message ?: "Unknown error"
        }
    }
}
