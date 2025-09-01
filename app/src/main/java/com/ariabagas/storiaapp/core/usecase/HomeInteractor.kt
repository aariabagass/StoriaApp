package com.ariabagas.storiaapp.core.usecase

import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeInteractor(private val repo: HomeRepository) : HomeUseCase {
    override fun getStories(token: String) = repo.getStories(token)

    override fun getStoryDetail(token: String, id: String): Flow<Story> = flow {
        emit(repo.getStoryDetail(token, id))
    }

    override fun addStory(token: String, description: String, photoPath: String): Flow<Boolean> = flow {
        emit(repo.addStory(token, description, photoPath))
    }
}