package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.KinopoiskMovie
import io.github.posaydone.filmix.core.model.KinopoiskMoviesResponse
import io.github.posaydone.filmix.core.network.service.KinopoiskService
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

    suspend fun getById(
        id: Int,
    ): KinopoiskMovie {
        return kinopoiskService.getById(id)
    }
}