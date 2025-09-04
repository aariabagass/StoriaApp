package com.ariabagas.storiaapp.data.network.services

import com.ariabagas.storiaapp.core.model.Login
import com.ariabagas.storiaapp.core.model.Register
import com.ariabagas.storiaapp.data.network.responses.AddStoryResponse
import com.ariabagas.storiaapp.data.network.responses.LoginResponse
import com.ariabagas.storiaapp.data.network.responses.RegisterResponse
import com.ariabagas.storiaapp.data.network.responses.StoriesResponse
import com.ariabagas.storiaapp.data.network.responses.StoryDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body body: Register
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body body: Login
    ): LoginResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1,
        @Query("size") size: Int = 20
    ): StoriesResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): StoryDetailResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStoryWithLocation(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): AddStoryResponse
}
