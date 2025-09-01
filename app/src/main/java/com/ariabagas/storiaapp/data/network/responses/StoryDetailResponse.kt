package com.ariabagas.storiaapp.data.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoryDetailResponse(
    @Json(name = "error") val error: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "story") val story: StoryItem
)