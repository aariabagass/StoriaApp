package com.ariabagas.storiaapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.usecase.HomeUseCase
import com.ariabagas.storiaapp.utils.ApiErrorHandler
import com.ariabagas.storiaapp.utils.ResultState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailViewModel(private val homeUseCase: HomeUseCase) : ViewModel() {
    private val _storyDetail = MutableLiveData<ResultState<Story>>()
    val storyDetail: LiveData<ResultState<Story>> = _storyDetail

    fun loadStoryDetail(token: String, id: String) {
        viewModelScope.launch {
            _storyDetail.postValue(ResultState.Loading)
            homeUseCase.getStoryDetail(token, id)
                .catch { e -> _storyDetail.postValue(ResultState.Error(ApiErrorHandler.getErrorMessage(e))) }
                .collect { story -> _storyDetail.postValue(ResultState.Success(story)) }
        }
    }
}
