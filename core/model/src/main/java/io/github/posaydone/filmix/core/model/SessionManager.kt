package io.github.posaydone.filmix.core.model

interface SessionManager {
    fun saveLoginState(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun clearSession()
}
