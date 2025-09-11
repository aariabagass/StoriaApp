package com.ariabagas.storiaapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.usecase.HomeUseCase

class HomeViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val refreshTrigger = MutableLiveData<Unit>()

    init {
        refreshStories()
    }

    fun refreshStories() {
        refreshTrigger.value = Unit
    }

    fun getStories(token: String): LiveData<PagingData<Story>> {
        return refreshTrigger.switchMap {
            homeUseCase.getStories(token)
                .cachedIn(viewModelScope)
                .asLiveData()
        }
    }
}
