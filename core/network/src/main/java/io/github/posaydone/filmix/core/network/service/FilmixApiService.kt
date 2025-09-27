package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.PageWithShows
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowProgressItem
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.core.model.UserProfileInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmixApiService {
    @GET("list")
    suspend fun fetchPage(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
        @Query("genre") genre: String? = null,
    ): PageWithShows<Show>

    @GET("viewing")
    suspend fun fetchViewingPage(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
    ): PageWithShows<Show>

    @GET("popular")
    suspend fun fetchPopularPage(
        @Query("page") page: Int? = null,
        @Query("section") section: FilmixCategory,
        @Query("limit") limit: Int = 48,
    ): PageWithShows<Show>

    @GET("last-seen")
    suspend fun fetchHistoryPage(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 10,
    ): PageWithShows<Show>

    @GET("last-seen")
    suspend fun fetchHistoryPageFull(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 10,
        @Query("full") full: Boolean = true,
    ): PageWithShows<ShowDetails>

    @GET("list")
    suspend fun fetchShowsListWithQuery(
        @Query("search") search: String = "",
        @Query("limit") limit: Int = 48,
    ): PageWithShows<Show>

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
    suspend fun fetchShowResource(
        @Path("movieId") movieId: Int,
    ): Response<ResponseBody>

    @GET("post/{movieId}/history")
    suspend fun fetchShowProgress(
        @Path("movieId") movieId: Int,
    ): ShowProgress

    @POST("post/{movieId}/history")
    suspend fun addShowProgress(
        @Path("movieId") movieId: Int, @Body showProgressItem: ShowProgressItem,
    )

    @POST("favorites")
    @FormUrlEncoded // Use this annotation for POST requests with x-www-form-urlencoded body
    suspend fun addToFavorites(
        @Field("id") showId: Int,
    ): Response<Unit>

    @GET("favorites")
    suspend fun fetchFavoritesPage(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 48,
    ): PageWithShows<Show>

    @GET("me")
    suspend fun fetchUserProfile(): UserProfileInfo

    @DELETE("favorites/{showId}")
    suspend fun removeFromFavorites(
        @Path("showId") showId: Int
    ): Response<Unit>
}
