package io.github.posaydone.filmix.core.network.interceptor

import io.github.posaydone.filmix.core.model.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "AuthInterceptor"

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sessionManager.fetchAccessToken()
        val hash = sessionManager.fetchHash()

        val requestBuilder = chain.request().newBuilder()
        accessToken?.let { requestBuilder.header("Authorization", "Bearer $it") }
        hash?.let { requestBuilder.header("Hash", it) }

        return chain.proceed(requestBuilder.build())
    }
}
