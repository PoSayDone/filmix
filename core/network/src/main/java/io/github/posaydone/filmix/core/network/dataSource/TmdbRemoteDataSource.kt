package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import io.github.posaydone.filmix.core.network.service.TmdbApiService
import javax.inject.Inject

class TmdbRemoteDataSource @Inject constructor(
    private val tmdbApiService: TmdbApiService
) {
    suspend fun searchMovies(
        query: String,
        page: Int? = null,
        language: String? = null
    ): TmdbSearchResponse {
        return tmdbApiService.searchMovies(
            query = query,
            page = page,
            language = language
        )
    }

    suspend fun searchTv(
        query: String,
        page: Int? = null,
        language: String? = null
    ): TmdbSearchResponse {
        return tmdbApiService.searchTv(
            query = query,
            page = page,
            language = language
        )
    }
}