package io.github.posaydone.filmix.core.network.interceptor

import android.util.Log
import io.github.posaydone.filmix.core.model.AuthEvent
import io.github.posaydone.filmix.core.model.RefreshRequestBody
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.network.service.AuthService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val authService: AuthService,
    private val authEventChannel: MutableSharedFlow<AuthEvent>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            triggerForceLogout()
            return null
        }

        synchronized(this) {
            val latestRefreshToken = sessionManager.fetchRefreshToken()
            if (latestRefreshToken == null) {
                Log.d(
                    TAG,
                    "Refresh token is null after acquiring lock; another thread likely failed. Aborting."
                )
                return null
            }

            val currentAccessToken = sessionManager.fetchAccessToken()
            val requestAccessToken =
                response.request.header("Authorization")?.substringAfter("Bearer ")

            if (currentAccessToken != null && currentAccessToken != requestAccessToken) {
                Log.d(TAG, "Token was already refreshed by another request. Retrying.")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            return try {
                Log.d(TAG, "This thread is performing the refresh.")
                val refreshResponse = runBlocking {
                    authService.refresh(RefreshRequestBody(latestRefreshToken))
                }

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newTokens = refreshResponse.body()!!
                    sessionManager.saveAccessToken(
                        newTokens.access_token,
                        System.currentTimeMillis() + (10 * 60 * 1000)
                    )
                    sessionManager.saveRefreshToken(newTokens.refresh_token)
                    Log.i(TAG, "Token refresh successful.")

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.access_token}")
                        .build()
                } else {
                    Log.e(TAG, "Refresh API call failed with code: ${refreshResponse.code()}")
                    triggerForceLogout()
                    null
                }

            } catch (e: Exception) {
                Log.e(TAG, "An exception occurred during token refresh.", e)
                triggerForceLogout()
                null
            }
        }
    }

    private fun triggerForceLogout() {
        sessionManager.clearTokens()
        runBlocking {
            Log.d(TAG, "Emitting ForceLogout event.")
            authEventChannel.tryEmit(AuthEvent.ForceLogout)
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
