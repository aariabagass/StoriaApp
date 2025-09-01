package com.ariabagas.storiaapp.core.repository

interface WelcomeRepository {
    suspend fun login(email: String, password: String): String
    suspend fun register(name: String, email: String, password: String): Boolean
}