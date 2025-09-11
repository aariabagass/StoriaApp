package com.ariabagas.storiaapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariabagas.storiaapp.core.usecase.WelcomeUseCase
import com.ariabagas.storiaapp.utils.ApiErrorHandler
import com.ariabagas.storiaapp.utils.ResultState
import com.ariabagas.storiaapp.utils.EspressoIdlingResource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoginViewModel(private val welcomeUseCase: WelcomeUseCase) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultState<String>>()
    val loginResult: LiveData<ResultState<String>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            EspressoIdlingResource.increment()
            _loginResult.postValue(ResultState.Loading)

            welcomeUseCase.login(email, password)
                .catch { e ->
                    val errorMessage = ApiErrorHandler.getErrorMessage(e)
                    _loginResult.postValue(ResultState.Error(errorMessage))
                    EspressoIdlingResource.decrement()
                }
                .collect { success ->
                    _loginResult.postValue(ResultState.Success(success))
                    EspressoIdlingResource.decrement()
                }
        }
    }
}
