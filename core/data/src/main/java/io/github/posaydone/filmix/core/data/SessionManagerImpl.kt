package io.github.posaydone.filmix.core.data

import android.content.Context
import android.content.SharedPreferences
import io.github.posaydone.filmix.core.model.SessionManager
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(context: Context) : SessionManager {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("Filmix", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_TOKEN_EXPIRES_IN = "user_token_expires_in"
    }

    override fun saveAccessToken(token: String, expiresIn: Long) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putLong(USER_TOKEN_EXPIRES_IN, expiresIn)
        editor.apply()
    }

    override fun saveRefreshToken(refresh: String) {
        val editor = prefs.edit()
        editor.putString(USER_REFRESH_TOKEN, refresh)
        editor.apply()
    }

    override fun fetchAccessToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    override fun fetchRefreshToken(): String? {
        return prefs.getString(USER_REFRESH_TOKEN, null)
    }

    override fun fetchTokenExpiresIn(): Long {
        return prefs.getLong(USER_TOKEN_EXPIRES_IN, Long.MIN_VALUE)
    }

    override fun isAccessTokenExpired(): Boolean {
        val currentTimeMills = System.currentTimeMillis()
        return fetchAccessToken() != null && currentTimeMills >= fetchTokenExpiresIn()
    }

    override fun clearTokens() {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, null)
        editor.putString(USER_REFRESH_TOKEN, null)
        editor.putLong(USER_TOKEN_EXPIRES_IN, Long.MIN_VALUE)
        editor.apply()
    }

}