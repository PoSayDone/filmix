package io.github.posaydone.filmix.core.model

data class HashResponse(
    val token: String,
    val code: String,
    val expire: Long
)
