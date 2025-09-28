package io.github.posaydone.filmix.tv.navigation

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    object Auth

    @Serializable
    object Main {
        @Serializable
        object Home

        @Serializable
        object Explore {
            @Serializable

            data class SearchResults(
                val query: String,
            )
        }

        @Serializable
        object Favorite
        
        @Serializable
        object Profile
        
        @Serializable
        object Settings

        @Serializable
        data class ShowsGrid(
            val queryType: String,
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
