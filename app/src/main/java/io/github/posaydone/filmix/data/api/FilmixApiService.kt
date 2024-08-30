package io.github.posaydone.filmix.data.api

import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.MovieList
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface FilmixApiService {

    @GET("list")
    suspend fun getList(
        @Query("search") search: String = "",
        @Query("category") category: String = "s7",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 48,
    ): MovieList

    @GET("post/{movieId}/details")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): MovieDetails

    @GET("post/{movieId}/video-links")
    suspend fun getSeriesOrMovie(
        @Path("movieId") movieId: Int,
    ): Response<ResponseBody>
}
