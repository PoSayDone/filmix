package io.github.posaydone.filmix.core.model

data class Show(
    val id: Int,
    val last_episode: Int?,
    val last_season: Int?,
    val original_name: String,
    val poster: String,
    val quality: String,
    val status: ShowStatus?,
    val title: String,
    val votesNeg: Int,
    val votesPos: Int,
    val year: Int,
    val url: String,
)

data class ShowStatus(
    val comment: String? = null,
    val status: Int? = null,
    val status_text: String? = null,
)