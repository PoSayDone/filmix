package io.github.posaydone.filmix.mobile.newNavigation.screen

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object MainGraph : Screen()

    @Serializable
    data class Player(val showId: Int) : Screen()
}
