package io.github.posaydone.filmix.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class MobileScreens {
    @Serializable
    object Auth

    @Serializable
    object Main {
        @Serializable
        object Home

        @Serializable
        object Explore

        @Serializable
        object History

        @Serializable
        data class SearchResults(
            val query: String,
        )

        @Serializable
        data class Details(
            val showId: Int,
        )
    }

    @Serializable
    data class Player(
        val showId: Int,
    )
}