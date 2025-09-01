package com.ariabagas.storiaapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.repository.HomeRepository
import com.ariabagas.storiaapp.data.network.services.ApiService
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData
import com.ariabagas.storiaapp.data.network.StoryPagingSource
import com.ariabagas.storiaapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class HomeRepositoryImpl(private val api: ApiService) : HomeRepository {
    override fun getStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { StoryPagingSource(api, token) }
        ).flow
    }

    override suspend fun getStoryDetail(token: String, id: String): Story {
        val response = api.getStoryDetail("Bearer $token", id)
        val item = response.story
        return Story(
            id = item.id,
            name = item.name,
            description = item.description,
            photoUrl = item.photoUrl,
            createdAt = item.createdAt,
            lat = item.lat,
            lon = item.lon
        )
    }

    override suspend fun addStory(
        token: String,
        description: String,
        photoPath: String
    ): Boolean {
        val file = File(photoPath).reduceFileImage()
        val descBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        val response = api.addStory("Bearer $token", multipartBody, descBody)
        return !response.error
    }
}