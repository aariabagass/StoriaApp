package com.ariabagas.storiaapp.core.usecase

import com.ariabagas.storiaapp.core.repository.WelcomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WelcomeInteractor(private val repo: WelcomeRepository) : WelcomeUseCase {
    override fun login(email: String, password: String): Flow<String> = flow {
        emit(repo.login(email, password))
    }

    override fun register(name: String, email: String, password: String): Flow<Boolean> = flow {
        emit(repo.register(name, email, password))
    }
}