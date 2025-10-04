package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.tmdb.TmdbFindResponse
import io.github.posaydone.filmix.core.model.tmdb.TmdbImagesResponse
import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import io.github.posaydone.filmix.core.network.service.TmdbApiService
import javax.inject.Inject

class TmdbRemoteDataSource @Inject constructor(
    private val tmdbApiService: TmdbApiService
) {
    suspend fun searchMovies(
        query: String,
        page: Int? = null,
        language: String? = null,
        year: Int? = null
    ): TmdbSearchResponse {
        return tmdbApiService.searchMovies(
            query = query,
            page = page,
            language = language,
            year = year
        )
    }

    suspend fun searchTv(
        query: String,
        page: Int? = null,
        language: String? = null,
        year: Int? = null
    ): TmdbSearchResponse {
        return tmdbApiService.searchTv(
            query = query,
            page = page,
            language = language,
            year = year
        )
    }

    suspend fun getMovieImages(
        movieId: Int,
        include_image_language: String? = null
    ): TmdbImagesResponse {
        return tmdbApiService.getMovieImages(
            movieId = movieId,
            include_image_language = include_image_language
        )
    }

    suspend fun getTvImages(
        tvId: Int,
        include_image_language: String? = null
    ): TmdbImagesResponse {
        return tmdbApiService.getTvImages(
            tvId = tvId,
            language = include_image_language
        )
    }

    suspend fun findByExternalId(
        externalId: String,
        externalSource: String = "imdb_id"
    ): TmdbFindResponse {
        return tmdbApiService.findByExternalId(
            externalId = externalId,
            externalSource = externalSource
        )
    }
}