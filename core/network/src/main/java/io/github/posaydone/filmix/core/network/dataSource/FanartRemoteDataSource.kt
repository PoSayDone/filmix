package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.fanart.FanartMovieResponse
import io.github.posaydone.filmix.core.model.fanart.FanartTvResponse
import io.github.posaydone.filmix.core.network.service.FanartApiService
import javax.inject.Inject

class FanartRemoteDataSource @Inject constructor(
    private val fanartApiService: FanartApiService,
) {
    suspend fun getTvShowImages(
        tvId: Int,
    ): FanartTvResponse {
        return fanartApiService.getTvShowImages(
            tvId = tvId,
        )
    }

    suspend fun getMovieImages(
        movieId: Int,
    ): FanartMovieResponse {
        return fanartApiService.getMovieImages(
            movieId = movieId,
        )
    }
}