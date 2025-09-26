package io.github.posaydone.filmix.core.model

import com.google.gson.annotations.SerializedName

/**
 * The top-level response object for a movie search query.
 */
data class KinopoiskMoviesResponse(
    @SerializedName("docs") val docs: List<KinopoiskMovie> = emptyList(),

    @SerializedName("total") val total: Int?,

    @SerializedName("limit") val limit: Int?,

    @SerializedName("page") val page: Int?,

    @SerializedName("pages") val pages: Int?,
)

/**
 * Represents a single movie or TV series item in the search results.
 */
data class KinopoiskMovie(
    @SerializedName("id") val id: Int?,

    @SerializedName("name") val name: String?,

    @SerializedName("alternativeName") val alternativeName: String?,

    @SerializedName("enName") val enName: String?,

    @SerializedName("type") val type: String,

    @SerializedName("year") val year: Int,

    @SerializedName("description") val description: String?,

    @SerializedName("shortDescription") val shortDescription: String?,

    @SerializedName("movieLength") val movieLength: Int?,

    @SerializedName("isSeries") val isSeries: Boolean,

    @SerializedName("seriesLength") val seriesLength: Int?,

    @SerializedName("totalSeriesLength") val totalSeriesLength: Int?,

    @SerializedName("ageRating") val ageRating: Int,

    @SerializedName("ratingMpaa") val ratingMpaa: String?,

    @SerializedName("top250") val top250: Int?,

    @SerializedName("status") val status: String?,

    @SerializedName("names") val names: List<NameObject> = emptyList(),

    @SerializedName("externalId") val externalId: ExternalId?,

    @SerializedName("logo") val logo: ImageObject?,

    @SerializedName("poster") val poster: ImageObject?,

    @SerializedName("backdrop") val backdrop: ImageObject?,

    @SerializedName("rating") val rating: Rating,

    @SerializedName("votes") val votes: Votes,

    @SerializedName("genres") val genres: List<KinopoiskGenre> = emptyList(),

    @SerializedName("countries") val countries: List<KinopoiskCountry> = emptyList(),

    @SerializedName("releaseYears") val releaseYears: List<ReleaseYear> = emptyList(),
)

/**
 * Represents various names and translations for a movie.
 */
data class NameObject(
    @SerializedName("name") val name: String?,

    @SerializedName("language") val language: String?,

    @SerializedName("type") val type: String?,
)

/**
 * Contains IDs from external movie databases.
 */
data class ExternalId(
    @SerializedName("imdb") val imdb: String?,

    @SerializedName("tmdb") val tmdb: Int?,

    @SerializedName("kpHD") val kpHD: String?,
)

/**

 * Represents an image with multiple resolutions. Used for logos, posters, and backdrops.
 */
data class ImageObject(
    @SerializedName("url") val url: String?,

    @SerializedName("previewUrl") val previewUrl: String?,
)

/**
 * Contains rating values from different sources.
 */
data class Rating(
    @SerializedName("kp") val kp: Double?,

    @SerializedName("imdb") val imdb: Double?,

    @SerializedName("filmCritics") val filmCritics: Double?,

    @SerializedName("russianFilmCritics") val russianFilmCritics: Double?,

    @SerializedName("await") val await: Double?,
)

/**
 * Contains vote counts from different sources.
 */
data class Votes(
    @SerializedName("kp") val kp: Int?,

    @SerializedName("imdb") val imdb: Int?,

    @SerializedName("filmCritics") val filmCritics: Int?,

    @SerializedName("russianFilmCritics") val russianFilmCritics: Int?,

    @SerializedName("await") val await: Int?,
)

/**
 * Represents a single genre.
 */
data class KinopoiskGenre(
    @SerializedName("name") val name: String,
)

/**
 * Represents a single country of origin.
 */
data class KinopoiskCountry(
    @SerializedName("name") val name: String,
)

/**
 * Represents the start and end years of a series' release.
 */
data class ReleaseYear(
    @SerializedName("start") val start: Int?,

    @SerializedName("end") val end: Int?,
)
