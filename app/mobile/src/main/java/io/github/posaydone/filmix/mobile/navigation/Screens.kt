package io.github.posaydone.filmix.mobile.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    object Auth

    @Serializable
    object Main {
        @Serializable
        object Home

        @Serializable
        object Explore

        @Serializable
        object Favorite

        @Serializable
        object Profile

        @Serializable
        object Settings {
            @Serializable
            object VideoQuality
            
            @Serializable
            object StreamType
            
            @Serializable
            object ServerLocation
        }

        @Serializable
        data class ShowsGrid(
            val queryType: String,
        )

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