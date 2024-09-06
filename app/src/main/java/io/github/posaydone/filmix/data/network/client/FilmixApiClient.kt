package io.github.posaydone.filmix.data.network.client

import android.content.Context
import android.util.Log
import io.github.posaydone.filmix.data.network.service.FilmixApiService
import io.github.posaydone.filmix.data.pref.SessionManager
import io.github.posaydone.filmix.data.util.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FilmixApiClient {

    private val TAG: String = "FilmixApiClient"
    private lateinit var apiService: FilmixApiService

    fun getApiService(context: Context): FilmixApiService {
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(okhttpClient(context))
                .build()

            apiService = retrofit.create(FilmixApiService::class.java)
        }

        return apiService
    }

    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(context)).build()
    }

    inner class AuthInterceptor(context: Context) : Interceptor {
        private val sessionManager = SessionManager(context)
        val filmixApiService = FilmixRefreshClient().getApiService(context)

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val accessToken = sessionManager.fetchAccessToken()
            val refreshToken = sessionManager.fetchRefreshToken()

            if (accessToken != null && refreshToken != null && sessionManager.isAccessTokenExpired()) {
                val refreshedToken = runBlocking {
                    val response = filmixApiService.refresh(refreshToken)
                    Log.d(TAG, "intercept: ${response}")
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

                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $refreshedToken").build()

                return chain.proceed(newRequest)
            }

            // Add the access token to the request header
            val authorizedRequest =
                originalRequest.newBuilder().header("Authorization", "Bearer $accessToken").build()

            return chain.proceed(authorizedRequest)
        }
    }
}
