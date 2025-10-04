package io.github.posaydone.filmix.core.model

import io.github.posaydone.filmix.core.model.tmdb.TmdbImage

/**
 * A DTO class that combines information from FilmixRepository, Kinopoisk, and TMDB services.
 * This provides a clean abstraction layer so the interface doesn't need to know which service
 * each piece of data comes from.
 *
 * @param id The unique identifier for the show
 * @param title The title of the show
 * @param originalTitle The original title of the show
 * @param year The release year
 * @param posterUrl The URL for the poster image
 * @param backdropUrl The URL for the backdrop image
 * @param logoUrl The URL for the logo image
 * @param description The description of the show
 * @param shortDescription A short description of the show
 * @param ratingKp The Kinopoisk rating
 * @param ratingImdb The IMDB rating
 * @param votesKp Kinopoisk votes count
 * @param votesImdb IMDB votes count
 * @param isSeries Whether this is a series or a movie
 * @param isShow Whether this is a show (has episodes) 
 * @param genres The list of genres
 * @param countries The list of countries
 * @param ageRating The age rating
 * @param movieLength The length of the movie in minutes
 * @param seriesLength The number of episodes in the series
 * @param quality The quality of the video
 * @param status The status of the show
 * @param votesPos Positive votes count
 * @param votesNeg Negative votes count
 * @param tmdbPosterPaths Poster paths from TMDB
 * @param tmdbBackdropPaths Backdrop paths from TMDB
 * @param tmdbLogoPaths Logo paths from TMDB
 */
data class FullShow(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val posterUrl: String,
    val backdropUrl: String,
    val logoUrl: String?,
    val description: String?,
    val shortDescription: String?,
    val ratingKp: Double?,
    val ratingImdb: Double?,
    val votesKp: Int?,
    val votesImdb: Int?,
    val isSeries: Boolean,
    val isShow: Boolean,
    val genres: List<String>,
    val countries: List<String>,
    val ageRating: Int,
    val movieLength: Int?,
    val seriesLength: Int?,
    val quality: String,
    val status: String?,
    val votesPos: Int,
    val votesNeg: Int,
    val tmdbPosterPaths: List<String>,
    val tmdbBackdropPaths: List<String>,
    val tmdbLogoPaths: List<String>,
) {
    companion object {
        /**
         * Creates a FullShow instance with minimal Filmix data when other services are unavailable
         */
        fun fromFilmixShow(show: Show, showDetails: ShowDetails?): FullShow {
            return FullShow(
                id = show.id,
                title = showDetails?.title ?: show.title,
                originalTitle = showDetails?.originalTitle ?: show.original_name,
                year = showDetails?.year ?: show.year,
                posterUrl = showDetails?.poster ?: show.poster,
                backdropUrl = show.poster, // Use poster as fallback for backdrop
                logoUrl = null,
                description = showDetails?.shortStory,
                shortDescription = null,
                ratingKp = showDetails?.ratingKinopoisk,
                ratingImdb = showDetails?.ratingImdb,
                votesKp = showDetails?.votesKinopoisk,
                votesImdb = showDetails?.votesIMDB,
                isSeries = showDetails?.lastEpisode != null,
                isShow = showDetails?.lastEpisode != null,
                genres = emptyList(),
                countries = emptyList(),
                ageRating = 0,
                movieLength = null,
                seriesLength = null,
                quality = showDetails?.quality ?: show.quality,
                status = showDetails?.status?.status_text,
                votesPos = show.votesPos,
                votesNeg = show.votesNeg,
                tmdbPosterPaths = emptyList(),
                tmdbBackdropPaths = emptyList(),
                tmdbLogoPaths = emptyList()
            )
        }
    }
}