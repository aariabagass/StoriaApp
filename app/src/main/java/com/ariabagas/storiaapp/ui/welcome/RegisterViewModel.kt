package com.ariabagas.storiaapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariabagas.storiaapp.core.usecase.WelcomeUseCase
import com.ariabagas.storiaapp.utils.ApiErrorHandler
import com.ariabagas.storiaapp.utils.ResultState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RegisterViewModel(private val welcomeUseCase: WelcomeUseCase) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultState<Boolean>>()
    val registerResult: LiveData<ResultState<Boolean>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.postValue(ResultState.Loading)
            welcomeUseCase.register(name, email, password)
                .catch { e ->
                    _registerResult.postValue(
                        ResultState.Error(
                            ApiErrorHandler.getErrorMessage(
                                e
                            )
                        )
                    )
                }
                .collect { success -> _registerResult.postValue(ResultState.Success(success)) }
        }
    }
}