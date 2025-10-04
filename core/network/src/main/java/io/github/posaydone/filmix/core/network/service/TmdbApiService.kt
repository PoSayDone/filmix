package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.tmdb.TmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("language") language: String? = null,
        @Query("primary_release_year") year: Int? = null
    ): TmdbSearchResponse

    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("language") language: String? = null,
        @Query("first_air_date_year") year: Int? = null
    ): TmdbSearchResponse

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("language") include_image_language: String? = null
    ): io.github.posaydone.filmix.core.model.tmdb.TmdbImagesResponse

    @GET("tv/{tv_id}/images")
    suspend fun getTvImages(
        @Path("tv_id") tvId: Int,
        @Query("language") language: String? = null
    ): io.github.posaydone.filmix.core.model.tmdb.TmdbImagesResponse

    @GET("find/{external_id}")
    suspend fun findByExternalId(
        @Path("external_id") externalId: String,
        @Query("external_source") externalSource: String = "imdb_id"
    ): io.github.posaydone.filmix.core.model.tmdb.TmdbFindResponse
}