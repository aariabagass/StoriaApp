package com.ariabagas.storiaapp.data.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoriesResponse(
    @Json(name = "error") val error: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "listStory") val listStory: List<StoryItem>
)

@JsonClass(generateAdapter = true)
data class StoryItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "photoUrl") val photoUrl: String,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?
)