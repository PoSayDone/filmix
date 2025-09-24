package io.github.posaydone.filmix.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.posaydone.filmix.core.model.SessionManager
import javax.inject.Inject
import javax.inject.Singleton


const val TAG = "SessionManager"

@Singleton
class SessionManagerImpl @Inject constructor(@ApplicationContext context: Context) : SessionManager {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("FilmixProxyPrefs", Context.MODE_PRIVATE)

    companion object {
        const val IS_LOGGED_IN = "is_logged_in"
    }

    override fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit {
            putBoolean(IS_LOGGED_IN, isLoggedIn)
        }
    }

    override fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    override fun clearSession() {
        prefs.edit {
            clear()
        }
    }
}
