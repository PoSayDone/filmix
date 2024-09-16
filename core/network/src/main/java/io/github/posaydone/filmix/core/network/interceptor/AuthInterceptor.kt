package io.github.posaydone.filmix.core.network.interceptor

import android.util.Log
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.network.service.FilmixRefreshService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "AuthInterceptor"

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val filmixRefreshService: FilmixRefreshService,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = sessionManager.fetchAccessToken()
        val refreshToken = sessionManager.fetchRefreshToken()

        if (accessToken != null && refreshToken != null && sessionManager.isAccessTokenExpired()) {
            val refreshedToken = runBlocking {
                val response = filmixRefreshService.refresh(refreshToken)
                Log.d(TAG, "response: ${response}")

                sessionManager.saveAccessToken(
                    response.accessToken, System.currentTimeMillis() + (50 * 60 * 1000)
                )
                sessionManager.saveRefreshToken(
                    response.refreshToken
                )
                response.accessToken
            }

            if (refreshedToken == null) {
                sessionManager.clearTokens()
                return chain.proceed(originalRequest)
            }

            val newRequest =
                originalRequest.newBuilder().header("Authorization", "Bearer $refreshedToken")
                    .build()

            return chain.proceed(newRequest)
        }

        val authorizedRequest =
            originalRequest.newBuilder().header("Authorization", "Bearer $accessToken").build()

        return chain.proceed(authorizedRequest)
    }
}
