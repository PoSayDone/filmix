package io.github.posaydone.filmix.core.model

interface SessionManager {
    fun saveAccessToken(token: String?, fallbackExpiration: Long)
    fun saveRefreshToken(refresh: String)
    fun saveHash(hash: String)
    fun fetchHash(): String?
    fun fetchAccessToken(): String?
    fun fetchRefreshToken(): String?
    fun fetchTokenExpiresIn(): Long
    fun isAccessTokenExpired(): Boolean
    fun clearTokens()
    fun removeAccessToken()
}