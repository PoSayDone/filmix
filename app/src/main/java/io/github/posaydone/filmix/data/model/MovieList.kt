package io.github.posaydone.filmix.data.model

data class MovieList(
    val has_next_page: Boolean,
    val items: List<MovieCard>,
    val page: Int,
    val status: String
)