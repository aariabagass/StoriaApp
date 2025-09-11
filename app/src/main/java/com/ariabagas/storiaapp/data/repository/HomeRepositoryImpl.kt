package com.ariabagas.storiaapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.repository.HomeRepository
import com.ariabagas.storiaapp.data.network.services.ApiService
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData
import com.ariabagas.storiaapp.data.local.room.AppDatabase
import com.ariabagas.storiaapp.data.network.responses.StoryItem
import com.ariabagas.storiaapp.data.remote.StoryRemoteMediator
import com.ariabagas.storiaapp.utils.reduceFileImage
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import androidx.paging.map
import com.ariabagas.storiaapp.data.local.room.entity.toDomain

class HomeRepositoryImpl(
    private val api: ApiService,
    private val database: AppDatabase
) : HomeRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(token: String): Flow<PagingData<Story>> {
        val pagingSourceFactory = { database.storyDao().getAllStories() }
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(database, api, token),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getStoriesWithLocation(token: String): Result<List<Story>> {
        return try {
            val response = api.getStoriesWithLocation("Bearer $token")
            val stories = response.listStory.map { it.toDomain() }
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    override suspend fun addStoryWithLocation(
        token: String,
        description: String,
        photoPath: String,
        lat: Double,
        lon: Double
    ): Boolean {
        val file = File(photoPath).reduceFileImage()
        val descBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

        val latBody = lat.toString().toRequestBody("text/plain".toMediaType())
        val lonBody = lon.toString().toRequestBody("text/plain".toMediaType())

        val response = api.addStoryWithLocation("Bearer $token", multipartBody, descBody, latBody, lonBody)
        return !response.error
    }

    private fun StoryItem.toDomain(): Story {
        return Story(
            id = id,
            name = name,
            description = description,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lat = lat,
            lon = lon
        )
    }
}