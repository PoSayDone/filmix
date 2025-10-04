package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.model.tmdb.TmdbFindResponse
import io.github.posaydone.filmix.core.model.tmdb.TmdbImagesResponse
import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import io.github.posaydone.filmix.core.network.dataSource.TmdbRemoteDataSource
import javax.inject.Inject

class TmdbRepository @Inject constructor(
    private val tmdbRemoteDataSource: TmdbRemoteDataSource
) {
    suspend fun searchMovies(
        query: String,
        page: Int? = null,
        language: String? = null,
        year: Int? = null
    ): TmdbSearchResponse {
        return tmdbRemoteDataSource.searchMovies(
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
        return tmdbRemoteDataSource.searchTv(
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
        return tmdbRemoteDataSource.getMovieImages(
            movieId = movieId,
            include_image_language = include_image_language
        )
    }

    suspend fun getTvImages(
        tvId: Int,
        include_image_language: String? = "ru-RU,en,null"
    ): TmdbImagesResponse {
        return tmdbRemoteDataSource.getTvImages(
            tvId = tvId,
            include_image_language = include_image_language
        )
    }

    suspend fun findByExternalId(
        externalId: String,
        externalSource: String = "imdb_id"
    ): TmdbFindResponse {
        return tmdbRemoteDataSource.findByExternalId(
            externalId = externalId,
            externalSource = externalSource
        )
    }
}