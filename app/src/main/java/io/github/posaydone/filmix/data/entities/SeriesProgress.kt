package io.github.posaydone.filmix.data.entities

data class SeriesProgress(
    val id: Int,
    val season: Int,
    val episode: Int,
    val translation: String,
    val quality: Int,
)
