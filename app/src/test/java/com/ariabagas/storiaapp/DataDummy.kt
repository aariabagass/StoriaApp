package com.ariabagas.storiaapp

import com.ariabagas.storiaapp.core.model.Story

object DataDummy {
    fun generateDummyStories(count: Int = 20): List<Story> {
        return (1..count).map { i ->
            Story(
                "story-$i",
                "Name $i",
                "Description$i",
                "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                "2025-01-01T00:00:00.000Z",
                0.0,
                0.0,
            )
        }
    }
}
