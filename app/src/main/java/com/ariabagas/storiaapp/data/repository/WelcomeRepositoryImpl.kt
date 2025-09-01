package com.ariabagas.storiaapp.data.repository

import com.ariabagas.storiaapp.core.model.Login
import com.ariabagas.storiaapp.core.model.Register
import com.ariabagas.storiaapp.core.repository.WelcomeRepository
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.data.network.services.ApiService

class WelcomeRepositoryImpl(
    private val api: ApiService,
    private val prefs: UserPreference
) : WelcomeRepository {

    override suspend fun login(email: String, password: String): String {
        val response = api.login(Login(email, password))
        if (!response.error) {
            val token = response.loginResult?.token ?: ""
            prefs.saveToken(token)
            return token
        } else throw Exception(response.message)
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {
        val response = api.register(Register(name, email, password))
        if (!response.error) return true else throw Exception(response.message)
    }
}