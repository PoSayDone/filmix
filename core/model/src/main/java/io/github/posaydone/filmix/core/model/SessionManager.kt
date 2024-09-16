package io.github.posaydone.filmix.core.model

interface SessionManager {
    fun saveAccessToken(token: String, expiresIn: Long)
    fun saveRefreshToken(refresh: String)
    fun fetchAccessToken(): String?
    fun fetchRefreshToken(): String?
    fun fetchTokenExpiresIn(): Long
    fun isAccessTokenExpired(): Boolean
    fun clearTokens()
}