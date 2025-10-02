package io.github.posaydone.filmix.mobile.newNavigation.screen

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

var navBarScreenItems = listOf<NavBarGraphData>(
    NavBarGraphData.Home,
    NavBarGraphData.Explore,
    NavBarGraphData.Favorite,
    NavBarGraphData.ProfileGraph,
)

@Serializable
sealed class NavBarGraphData(val icon: String, val title: String): NavKey {
    @Serializable
    data object Home : NavBarGraphData(
        title = "Home", icon = "Home"
    )

    @Serializable
    data object Explore : NavBarGraphData(
        title = "Explore", icon = "Explore"
    )

    @Serializable
    data object Favorite : NavBarGraphData(
        title = "Favorite", icon = "Favorite"
    )

    @Serializable
    data object ProfileGraph : NavBarGraphData(
        title = "Profile", icon = "Profile"
    )
}
