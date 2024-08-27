package io.github.posaydone.filmix.data.model

data class MovieCard (
    val id: Int,
    val last_episode: Any,
    val last_season: Any,
    val original_name: String,
    val poster: String,
    val quality: String,
    val status: Any,
    val title: String,
    val votesNeg: Int,
    val votesPos: Int,
    val year: Int
)