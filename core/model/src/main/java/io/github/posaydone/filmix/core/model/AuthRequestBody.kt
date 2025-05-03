package io.github.posaydone.filmix.core.model

data class AuthRequestBody(
    val user_name: String,
    val user_passw: String,
    val session: Boolean
)
