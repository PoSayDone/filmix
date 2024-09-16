package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.RefreshTokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FilmixRefreshService {
    @GET("refresh")
    suspend fun refresh(
        @Query("refreshToken") refreshToken: String,
    ): RefreshTokenResponse
}