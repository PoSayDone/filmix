package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.model.fanart.FanartMovieResponse
import io.github.posaydone.filmix.core.model.fanart.FanartTvResponse
import io.github.posaydone.filmix.core.network.dataSource.FanartRemoteDataSource
import javax.inject.Inject

class FanartRepository @Inject constructor(
    private val fanartRemoteDataSource: FanartRemoteDataSource,
) {
    suspend fun getTvShowImages(
        tvId: Int,
    ): FanartTvResponse {
        return fanartRemoteDataSource.getTvShowImages(
            tvId = tvId,
        )
    }

    suspend fun getMovieImages(
        movieId: Int,
    ): FanartMovieResponse {
        return fanartRemoteDataSource.getMovieImages(
            movieId = movieId,
        )
    }
}