package io.github.posaydone.filmix.core.data.repository

import io.github.posaydone.filmix.core.model.KinopoiskMoviesResponse
import io.github.posaydone.filmix.core.network.dataSource.KinopoiskRemoteDataSource
import javax.inject.Inject

class KinopoiskRepository @Inject constructor(private val kinopoiskRemoteDataSource: KinopoiskRemoteDataSource) {
    suspend fun movieSearch(
        page: Int? = 1,
        limit: Int? = 10,
        query: String,
    ): KinopoiskMoviesResponse {
        return kinopoiskRemoteDataSource.movieSearch(page, limit, query)
    }
}