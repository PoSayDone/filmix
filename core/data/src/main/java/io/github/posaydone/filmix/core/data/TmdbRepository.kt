package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import io.github.posaydone.filmix.core.network.dataSource.TmdbRemoteDataSource
import javax.inject.Inject

class TmdbRepository @Inject constructor(
    private val tmdbRemoteDataSource: TmdbRemoteDataSource
) {
    suspend fun searchMovies(
        query: String,
        page: Int? = null,
        language: String? = null
    ): TmdbSearchResponse {
        return tmdbRemoteDataSource.searchMovies(
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
        return tmdbRemoteDataSource.searchTv(
            query = query,
            page = page,
            language = language
        )
    }
}