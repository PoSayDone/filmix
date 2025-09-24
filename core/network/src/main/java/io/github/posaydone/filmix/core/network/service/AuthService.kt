package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth")
    suspend fun login(@Body body: AuthRequestBody): Response<MessageResponse>

    @POST("logout")
    suspend fun logout(): Response<MessageResponse>
}