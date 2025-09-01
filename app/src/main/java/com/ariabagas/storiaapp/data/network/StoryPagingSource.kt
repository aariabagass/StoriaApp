package com.ariabagas.storiaapp.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.data.network.services.ApiService

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getStories("Bearer $token", page, params.loadSize)

            val stories = response.listStory.map {
                Story(it.id, it.name, it.description, it.photoUrl, createdAt = it.createdAt)
            }

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { pos ->
            val anchorPage = state.closestPageToPosition(pos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
