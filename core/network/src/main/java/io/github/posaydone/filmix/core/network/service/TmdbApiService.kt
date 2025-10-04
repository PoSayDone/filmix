package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("language") language: String? = null
    ): TmdbSearchResponse

    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("language") language: String? = null
    ): TmdbSearchResponse
}