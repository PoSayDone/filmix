package io.github.posaydone.filmix.data.api

import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.SeriesHistory
import io.github.posaydone.filmix.data.model.ShowsPage
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmixApiService {
    @GET("post/{movieId}/history")
    suspend fun getShowHistory(
        @Path("movieId") movieId: Int,
    ): List<SeriesHistory?>

    @POST("post/{movieId}/history")
    suspend fun setShowHistory(
        @Path("movieId") movieId: Int,
        @Body seriesHistory: SeriesHistory
    )

    @GET("list")
    suspend fun getList(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
        @Query("genre") genre: String? = null,
    ): ShowsPage

    @GET("viewing")
    suspend fun getNew(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("popular")
    suspend fun getPopular(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("list")
    suspend fun getSearch(
        @Query("search") search: String = "",
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("post/{movieId}/details")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): MovieDetails

    @GET("post/{movieId}/video-links")
    suspend fun getSeriesOrMovie(
        @Path("movieId") movieId: Int,
    ): Response<ResponseBody>
}
