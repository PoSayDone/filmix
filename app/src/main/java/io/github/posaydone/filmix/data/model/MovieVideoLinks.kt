package io.github.posaydone.filmix.data.model

import androidx.annotation.Keep

data class File(
    val url: String,
    val quality: Int,
    val proPlus: Boolean
)

data class Episode(
    val episode: Int,
    val ad_skip: Int,
    val title: String,
    val released: String,
    val files: List<File>
)


data class Season(
    val season: Int,
    val episodes: Map<String, Episode>
)

@Keep
class Translation : LinkedHashMap<String, Season>()

@Keep
class Series : LinkedHashMap<String, Translation>()

data class MoviePiece(
    val season: String,
    val episode: String,
    val adSkip: Int,
    val title: String,
    val released: String,
    val files: List<File>,
    val voiceover: String,
    val updated: Int,
    val uk: Boolean,
    val type: String,
)

sealed class MovieOrSeriesResponse {
    data class SeriesResponse(val series: Series) : MovieOrSeriesResponse()
    data class MovieResponse(val movies: List<MoviePiece>) : MovieOrSeriesResponse()
}
