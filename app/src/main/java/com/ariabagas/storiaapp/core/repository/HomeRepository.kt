package com.ariabagas.storiaapp.core.repository

import androidx.paging.PagingData
import com.ariabagas.storiaapp.core.model.Story
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getStories(token: String): Flow<PagingData<Story>>
    suspend fun getStoryDetail(token: String, id: String): Story
    suspend fun addStory(token: String, description: String, photoPath: String): Boolean
}