package com.ariabagas.storiaapp.core.usecase

import androidx.paging.PagingData
import com.ariabagas.storiaapp.core.model.Story
import kotlinx.coroutines.flow.Flow


interface HomeUseCase {
    fun getStories(token: String): Flow<PagingData<Story>>
    fun getStoryDetail(token: String, id: String): Flow<Story>
    fun addStory(token: String, description: String, photoPath: String): Flow<Boolean>
}