package io.github.posaydone.filmix.core.network.interceptor

import android.util.Log
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.network.service.FilmixAuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val filmixAuthService: FilmixAuthService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop
        if (responseCount(response) >= 2) {
            sessionManager.clearTokens()
            return null // Give up, token refresh failed
        }

        val refreshToken = sessionManager.fetchRefreshToken()
        val hash = sessionManager.fetchHash()

        if (refreshToken.isNullOrEmpty() || hash.isNullOrEmpty()) {
            sessionManager.clearTokens()
            return null
        }

        // Check if access token is expired before refreshing
        if (!sessionManager.isAccessTokenExpired() && response.code != 401) {
            val accessToken = sessionManager.fetchAccessToken()
            return accessToken?.let {
                response.request.newBuilder().header("Authorization", "Bearer $it").apply {
                    if (hash.isNotEmpty()) header("Hash", hash)
                }.build()
            }
        }

        // Token is expired, try refreshing
        return try {
            runBlocking {
                val refreshResponse =
                    filmixAuthService.refresh(refreshToken, hash)

                sessionManager.saveAccessToken(
                    refreshResponse.accessToken,
                    System.currentTimeMillis() + (10 * 60 * 1000) // You may want to use actual expiresIn from API
                )
                sessionManager.saveRefreshToken(refreshResponse.refreshToken)
                Log.d("TokenAuthenticator", "Refreshed token")

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.accessToken}").apply {
                        if (!hash.isNullOrEmpty()) header("Hash", hash)
                    }.build()
            }

        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "Refresh failed: ${e.message}", e)
            sessionManager.clearTokens()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var res = response
        var count = 1
        while (res.priorResponse != null) {
            count++
            res = res.priorResponse!!
        }
        return count
    }
}
