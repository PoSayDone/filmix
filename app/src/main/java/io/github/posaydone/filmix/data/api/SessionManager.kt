package io.github.posaydone.filmix.data.api

import android.content.Context
import android.content.SharedPreferences
import io.github.posaydone.filmix.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_TOKEN_EXPIRES_IN = "user_token_expires_in"
    }

    fun saveAccessToken(token: String, expiresIn: Long) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putLong(USER_TOKEN_EXPIRES_IN, expiresIn)
        editor.apply()
    }

    fun saveRefreshToken(refresh: String) {
        val editor = prefs.edit()
        editor.putString(USER_REFRESH_TOKEN, refresh)
        editor.apply()
    }

    fun fetchAccessToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString(USER_REFRESH_TOKEN, null)
    }

    fun fetchTokenExpiresIn(): Long {
        return prefs.getLong(USER_TOKEN_EXPIRES_IN, Long.MAX_VALUE)
    }

    fun isAccessTokenExpired(): Boolean {
        val currentTimeMills = System.currentTimeMillis()
        return fetchAccessToken() != null && currentTimeMills >= fetchTokenExpiresIn()
    }

    fun clearTokens() {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, null)
        editor.putString(USER_REFRESH_TOKEN, null)
        editor.putLong(USER_TOKEN_EXPIRES_IN, Long.MAX_VALUE)
        editor.apply()
    }

}
