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

class MapViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<ResultState<List<Story>>>()
    val storiesWithLocation: LiveData<ResultState<List<Story>>> = _storiesWithLocation

    fun loadStoriesWithLocation(token: String) {
        viewModelScope.launch {
            _storiesWithLocation.postValue(ResultState.Loading)
            homeUseCase.getStoriesWithLocation(token)
                .catch { e ->
                    _storiesWithLocation.postValue(
                        ResultState.Error(ApiErrorHandler.getErrorMessage(e))
                    )
                }
                .collect { result ->
                    result.onSuccess { stories ->
                        _storiesWithLocation.postValue(ResultState.Success(stories))
                    }
                }
        }
    }
}