package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixAuthRepository
import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.HashResponse
import io.github.posaydone.filmix.core.model.QrResponse
import io.github.posaydone.filmix.core.model.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val repository: FilmixAuthRepository
) : ViewModel() {

    suspend fun authorizeUser(hash: String, body: AuthRequestBody): AuthResponse {
        return withContext(Dispatchers.IO) {
            repository.authorize(hash, body)
        }
    }

    suspend fun requestQrCode(hash: String): QrResponse {
        return withContext(Dispatchers.IO) {
            repository.getQrCode(hash)
        }
    }

    suspend fun requestHash(): HashResponse {
        return withContext(Dispatchers.IO) {
            repository.getHash()
        }
    }

    fun saveTokens(access: String, refresh: String, hash: String, expiresInMs: Long) {
        sessionManager.saveAccessToken(access, System.currentTimeMillis() + expiresInMs)
        sessionManager.saveRefreshToken(refresh)
        sessionManager.saveHash(hash)
    }

    fun areTokensSaved(): Boolean {
        return sessionManager.fetchAccessToken() != null &&
                sessionManager.fetchRefreshToken() != null &&
                sessionManager.fetchHash() != null
    }
}
