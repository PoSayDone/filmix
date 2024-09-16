package io.github.posaydone.filmix.core.model

typealias ShowProgress = List<ShowProgressItem>

data class ShowProgressItem(
    val season: Int,
    val episode: Int,
    val voiceover: String,
    val time: Long,
    val quality: Int,
)