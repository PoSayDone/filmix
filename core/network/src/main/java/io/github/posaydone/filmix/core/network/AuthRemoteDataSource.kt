package io.github.posaydone.filmix.core.network

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.network.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
) {

    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = authService.login(AuthRequestBody(username, password))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        authService.logout()
    }
}
