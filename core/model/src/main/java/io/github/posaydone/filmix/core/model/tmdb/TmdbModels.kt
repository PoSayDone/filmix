package io.github.posaydone.filmix.core.model.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbSearchResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbSearchResult>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TmdbSearchResult(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("media_type") val mediaType: String
)

data class TmdbImagesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("backdrops") val backdrops: List<TmdbImage>?,
    @SerializedName("posters") val posters: List<TmdbImage>?,
    @SerializedName("logos") val logos: List<TmdbImage>?,
    @SerializedName("profiles") val profiles: List<TmdbImage>?
)

data class TmdbImage(
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("aspect_ratio") val aspectRatio: Double?,
    @SerializedName("height") val height: Int?,
    @SerializedName("width") val width: Int?,
    @SerializedName("iso_639_1") val iso6391: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("file_type") val fileType: String?
)

data class TmdbFindResponse(
    @SerializedName("movie_results") val movieResults: List<TmdbSearchResult> = emptyList(),
    @SerializedName("tv_results") val tvResults: List<TmdbSearchResult> = emptyList(),
    @SerializedName("person_results") val personResults: List<TmdbPersonResult> = emptyList()
)

data class TmdbPersonResult(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("popularity") val popularity: Double?
)