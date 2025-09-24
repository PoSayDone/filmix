package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.MessageResponse
import io.github.posaydone.filmix.core.model.RefreshRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/sign-in")
    suspend fun login(@Body body: AuthRequestBody): AuthResponse

    @POST("auth/logout")
    suspend fun logout(): Response<MessageResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body refreshToken: RefreshRequestBody): Response<AuthResponse>
    
}