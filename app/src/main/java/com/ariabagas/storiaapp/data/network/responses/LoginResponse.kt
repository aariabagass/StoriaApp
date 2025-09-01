package com.ariabagas.storiaapp.data.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "error") val error: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "loginResult") val loginResult: LoginResult?
) {
    @JsonClass(generateAdapter = true)
    data class LoginResult(
        @Json(name = "userId") val userId: String,
        @Json(name = "name") val name: String,
        @Json(name = "token") val token: String
    )
}