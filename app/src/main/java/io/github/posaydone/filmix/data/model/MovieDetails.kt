package io.github.posaydone.filmix.data.model

data class MovieDetails(
    val actors: Any,
    val category: String,
    val countries: List<Country>,
    val directors: Any,
    val duration: Int,
    val genres: List<Genre>,
    val id: Int,
    val idKinopoisk: Int,
    val is_deferred: Boolean,
    val is_favorite: Boolean,
    val is_hdr: Boolean,
    val last_episode: Any,
    val max_episode: Any,
    val mpaa: String,
    val original_title: String,
    val poster: String,
    val quality: String,
    val ratingImdb: Double,
    val ratingKinopoisk: Double,
    val rip: String,
    val short_story: String,
    val slogan: String,
    val status: Any,
    val title: String,
    val updated: String,
    val url: String,
    val votesIMDB: Int,
    val votesKinopoisk: Int,
    val votesNeg: Int,
    val votesPos: Int,
    val year: Int
)

data class Genre(
    val alt_name: String,
    val id: Int,
    val name: String
)
data class Country(
    val id: Int,
    val name: String
)
