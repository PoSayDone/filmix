package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.KinopoiskMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KinopoiskService {
    @GET("movie/search")
    suspend fun movieSearch(
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10,
        @Query("query") query: String,
    ): KinopoiskMoviesResponse
}
