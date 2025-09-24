package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.network.AuthRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
) {

    suspend fun login(username: String, password: String): AuthResponse {
        return remoteDataSource.login(username, password)
    }

    suspend fun logout() {
        remoteDataSource.logout()
    }
}
