package io.github.posaydone.filmix.data.model

import androidx.annotation.Keep

sealed class MovieOrSeriesResponse {
    data class SeriesResponse(val series: Series) : MovieOrSeriesResponse()
    data class MovieResponse(val movies: List<MovieTranslation>) : MovieOrSeriesResponse()
}

@Keep
class FilmixSeries : LinkedHashMap<String, FilmixTranslation>()

@Keep
class FilmixTranslation : LinkedHashMap<String, FilmixSeason>()

data class FilmixSeason(
    val season: Int,
    val episodes: Map<String, FilmixEpisode>
)

data class FilmixEpisode(
    val episode: Int,
    val ad_skip: Int,
    val title: String,
    val released: String,
    val files: List<File>
)


data class MovieTranslation(
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
) {
    override fun toString(): String = voiceover
}

data class Series(
    val seasons: List<Season>
)

data class Season(
    val season: Int,
    val episodes: MutableList<Episode>
) {
    override fun toString(): String = season.toString()
}

data class Episode(
    val episode: Int,
    val ad_skip: Int,
    val title: String,
    val released: String,
    val translations: MutableList<Translation>
) {
    override fun toString(): String = "Серия ${episode}"
}

data class Translation(
    val translation: String,
    val files: List<File>
) {
    override fun toString(): String = translation
}

data class File(
    val url: String,
    val quality: Int,
    val proPlus: Boolean,
) {
    override fun toString(): String = quality.toString()
}
