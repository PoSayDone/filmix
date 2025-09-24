package io.github.posaydone.filmix.core.network.interceptor

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesCookieJar @Inject constructor(
    @ApplicationContext context: Context,
) : CookieJar {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE)

    companion object {
        private const val SESSION_COOKIE_KEY = "session_cookie"
        private const val SESSION_COOKIE_NAME = "session"
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val sessionCookie = cookies.firstOrNull { it.name == SESSION_COOKIE_NAME }
        if (sessionCookie != null) {
            prefs.edit {
                putString(SESSION_COOKIE_KEY, sessionCookie.toString())
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieString = prefs.getString(SESSION_COOKIE_KEY, null)
        if (!cookieString.isNullOrBlank()) {
            val cookie = Cookie.parse(url, cookieString)
            if (cookie != null) {
                return listOf(cookie)
            }
        }
        return emptyList()
    }
}
