package com.ariabagas.storiaapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.usecase.HomeUseCase
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {
    fun getStories(token: String): Flow<PagingData<Story>> =
        homeUseCase.getStories(token).cachedIn(viewModelScope)
}
