package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.network.FilmixAuthRemoteDataSource
import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.HashResponse
import io.github.posaydone.filmix.core.model.QrResponse
import javax.inject.Inject

class FilmixAuthRepository @Inject constructor(
    private val remoteDataSource: FilmixAuthRemoteDataSource
) {
    suspend fun authorize(hash: String, body: AuthRequestBody): AuthResponse {
        return remoteDataSource.authorize(hash, body)
    }

    suspend fun getHash(): HashResponse {
        return remoteDataSource.requireHash()
    }

    suspend fun getQrCode(hash: String): QrResponse {
        return remoteDataSource.requireQr(hash)
    }
}
