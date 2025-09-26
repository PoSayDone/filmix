package io.github.posaydone.filmix.core.network

import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.core.model.AuthResponse
import io.github.posaydone.filmix.core.model.KinopoiskMoviesResponse
import io.github.posaydone.filmix.core.network.service.KinopoiskService
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class KinopoiskRemoteDataSource @Inject constructor(
    private val kinopoiskService: KinopoiskService,
) {
    suspend fun movieSearch(
        page: Int? = 1,
        limit: Int? = 10,
        query: String,
    ): KinopoiskMoviesResponse {
        return kinopoiskService.movieSearch(page, limit, query)
    }
}