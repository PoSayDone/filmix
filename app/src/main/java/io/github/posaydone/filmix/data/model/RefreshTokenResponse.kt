package io.github.posaydone.filmix.data.model

data class RefreshTokenResponse(
    val user_id: Int,
    val is_pro: Boolean,
    val is_pro_plus: Boolean,
    val pro_date: String,
    val accessToken: String,
    val refreshToken: String,
    val ga: Boolean
)
