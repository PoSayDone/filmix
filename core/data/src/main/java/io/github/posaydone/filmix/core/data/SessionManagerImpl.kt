package io.github.posaydone.filmix.core.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.auth0.android.jwt.JWT
import io.github.posaydone.filmix.core.model.SessionManager
import javax.inject.Inject

const val TAG = "SessionManager"

fun extractExpirationTimeFromJwt(token: String): Long? {
    return try {
        val jwt = JWT(token)
        jwt.expiresAt?.time
    } catch (e: Exception) {
        null
    }
}

class SessionManagerImpl @Inject constructor(context: Context) : SessionManager {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("Filmix", Context.MODE_PRIVATE)

    companion object {
        const val USER_HASH = "user_hash"
        const val USER_TOKEN = "user_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_TOKEN_EXPIRES_IN = "user_token_expires_in"
    }

    override fun saveHash(hash: String) {
        prefs.edit() {
            putString(USER_HASH, hash)
        }
    }

    override fun saveAccessToken(token: String?, fallbackExpiration: Long) {
        val expiration = token?.let { extractExpirationTimeFromJwt(it) } ?: fallbackExpiration
        Log.d(TAG, "saveAccessToken: ${expiration}, $fallbackExpiration")
        prefs.edit() {
            putString(USER_TOKEN, token)
            putLong(USER_TOKEN_EXPIRES_IN, fallbackExpiration)
        }
    }

    override fun saveRefreshToken(refresh: String) {
        prefs.edit() {
            putString(USER_REFRESH_TOKEN, refresh)
        }
    }

    override fun fetchHash(): String? {
        return prefs.getString(USER_HASH, null)
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

    override fun removeAccessToken() {
        prefs.edit {
            putString(USER_TOKEN, null)
        }
    }

    override fun clearTokens() {
        prefs.edit {
            putString(USER_TOKEN, null)
            putString(USER_REFRESH_TOKEN, null)
            putString(USER_HASH, null)
            putLong(USER_TOKEN_EXPIRES_IN, Long.MIN_VALUE)
        }
    }

}