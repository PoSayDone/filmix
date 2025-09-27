package io.github.posaydone.filmix.core.model

interface SessionManager {
    fun saveAccessToken(token: String?, fallbackExpiration: Long)
    fun saveRefreshToken(refresh: String)
    fun fetchAccessToken(): String?
    fun fetchRefreshToken(): String?
    fun isAccessTokenExpired(): Boolean
    fun isLoggedIn(): Boolean
    fun logout()
}
