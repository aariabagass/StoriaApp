package com.ariabagas.storiaapp.core.usecase

import kotlinx.coroutines.flow.Flow

interface WelcomeUseCase {
    fun login(email: String, password: String): Flow<String>
    fun register(name: String, email: String, password: String): Flow<Boolean>
}