package com.ariabagas.storiaapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ariabagas.storiaapp.core.model.Story

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String?,
    val lat: Double?,
    val lon: Double?
)

fun StoryEntity.toDomain(): Story {
    return Story(
        id = this.id,
        name = this.name,
        description = this.description,
        photoUrl = this.photoUrl,
        createdAt = this.createdAt,
        lat = this.lat,
        lon = this.lon
    )
}