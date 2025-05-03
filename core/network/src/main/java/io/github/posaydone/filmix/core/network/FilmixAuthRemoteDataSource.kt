package io.github.posaydone.filmix.core.network

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.HashResponse
import io.github.posaydone.filmix.core.model.QrResponse
import io.github.posaydone.filmix.core.network.service.FilmixAuthService
import javax.inject.Inject

class FilmixAuthRemoteDataSource @Inject constructor(
    private val api: FilmixAuthService
) {
    suspend fun authorize(hash: String, body: AuthRequestBody): AuthResponse {
        return api.authorize(hash, body)
    }

    suspend fun requireHash(): HashResponse {
        return api.requireHash()
    }

    suspend fun requireQr(hash: String): QrResponse {
        return api.requireQr(hash)
    }
}
