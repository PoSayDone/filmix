package io.github.posaydone.filmix.data.entities

data class ShowImages(
    val frames: List<ShowImage>,
    val posters: List<ShowImage>
)

data class ShowImage(
    val size: Int,
    val title: String,
    val url: String
)
