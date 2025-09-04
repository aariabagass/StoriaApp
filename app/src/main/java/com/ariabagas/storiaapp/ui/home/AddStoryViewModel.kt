package com.ariabagas.storiaapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariabagas.storiaapp.core.usecase.HomeUseCase
import com.ariabagas.storiaapp.utils.ApiErrorHandler
import com.ariabagas.storiaapp.utils.ResultState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _uploadResult = MutableLiveData<ResultState<Boolean>>()
    val uploadResult: LiveData<ResultState<Boolean>> = _uploadResult

    fun uploadStory(token: String, description: String, photoFile: File) {
        viewModelScope.launch {
            _uploadResult.postValue(ResultState.Loading)
            homeUseCase.addStory(token, description, photoFile.path)
                .catch { e ->
                    _uploadResult.postValue(ResultState.Error(ApiErrorHandler.getErrorMessage(e)))
                }
                .collect { success ->
                    _uploadResult.postValue(ResultState.Success(success))
                }
        }
    }

    fun uploadStoryWithLocation(
        token: String,
        description: String,
        photoFile: File,
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch {
            _uploadResult.postValue(ResultState.Loading)
            homeUseCase.addStoryWithLocation(token, description, photoFile.path, lat, lon)
                .catch { e ->
                    _uploadResult.postValue(ResultState.Error(ApiErrorHandler.getErrorMessage(e)))
                }
                .collect { success ->
                    _uploadResult.postValue(ResultState.Success(success))
                }
        }
    }
}
