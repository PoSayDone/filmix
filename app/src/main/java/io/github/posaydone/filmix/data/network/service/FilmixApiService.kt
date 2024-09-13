package io.github.posaydone.filmix.data.network.service

import io.github.posaydone.filmix.data.entities.FilmixCategory
import io.github.posaydone.filmix.data.entities.LastSeenPage
import io.github.posaydone.filmix.data.entities.ShowDetails
import io.github.posaydone.filmix.data.entities.ShowHistoryItem
import io.github.posaydone.filmix.data.entities.ShowImages
import io.github.posaydone.filmix.data.entities.ShowTrailers
import io.github.posaydone.filmix.data.entities.ShowsPage
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmixApiService {
    @GET("post/{movieId}/history")
    suspend fun fetchShowHistory(
        @Path("movieId") movieId: Int,
    ): List<ShowHistoryItem?>

    @POST("post/{movieId}/history")
    suspend fun setShowHistory(
        @Path("movieId") movieId: Int, @Body showHistoryItem: ShowHistoryItem,
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
    suspend fun fetchViewingShowsList(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("popular")
    suspend fun fetchPopularShowsList(
        @Query("page") page: Int? = null,
        @Query("section") section: FilmixCategory,
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("last-seen")
    suspend fun fetchLastSeenListFull(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 10,
        @Query("full") full: Boolean? = null,
    ): LastSeenPage

    @GET("last-seen")
    suspend fun fetchLastSeenList(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 10,
    ): ShowsPage

    @GET("list")
    suspend fun fetchShowsListWithQuery(
        @Query("search") search: String = "",
        @Query("limit") limit: Int = 48,
    ): ShowsPage

    @GET("post/{movieId}/details")
    suspend fun fetchShowDetails(@Path("movieId") movieId: Int): ShowDetails


    @GET("post/{movieId}/images")
    suspend fun fetchShowImages(
        @Path("movieId") movieId: Int,
    ): ShowImages


    @GET("post/{movieId}/trailers")
    suspend fun fetchShowTrailers(
        @Path("movieId") movieId: Int,
    ): ShowTrailers

    @GET("post/{movieId}/video-links")
    suspend fun fetchShow(
        @Path("movieId") movieId: Int,
    ): Response<ResponseBody>
}
