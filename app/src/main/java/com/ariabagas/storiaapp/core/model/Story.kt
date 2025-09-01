package com.ariabagas.storiaapp.core.model

import com.squareup.moshi.Json

data class Story(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "photoUrl") val photoUrl: String,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "lat") val lat: Double? = null,
    @Json(name = "lon") val lon: Double? = null
)