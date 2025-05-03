package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.HashResponse
import io.github.posaydone.filmix.core.model.QrResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FilmixAuthService {
    @GET("auth-qr")
    suspend fun requireQr(
        @retrofit2.http.Header("Hash") hash: String,
    ): QrResponse

    @GET("request-token")
    suspend fun requireHash(): HashResponse

    @POST("auth")
    suspend fun authorize(
        @retrofit2.http.Header("Hash") hash: String,
        @Body body: AuthRequestBody,
    ): AuthResponse

    @GET("refresh")
    suspend fun refresh(
        @Query("refreshToken") refreshToken: String,
        @retrofit2.http.Header("Hash") hash: String,
    ): AuthResponse
}