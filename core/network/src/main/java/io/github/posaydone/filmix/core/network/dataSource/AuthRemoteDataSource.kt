package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.network.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
) {

    suspend fun login(username: String, password: String): AuthResponse {
        return authService.login(AuthRequestBody(username, password))
    }

    suspend fun logout() {
        authService.logout()
    }
}
