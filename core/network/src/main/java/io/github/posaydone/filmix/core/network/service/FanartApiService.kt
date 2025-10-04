package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.fanart.FanartMovieResponse
import io.github.posaydone.filmix.core.model.fanart.FanartTvResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FanartApiService {
    @GET("tv/{tv_id}")
    suspend fun getTvShowImages(
        @Path("tv_id") tvId: Int,
    ): FanartTvResponse

    @GET("movies/{movie_id}")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int,
    ): FanartMovieResponse
}