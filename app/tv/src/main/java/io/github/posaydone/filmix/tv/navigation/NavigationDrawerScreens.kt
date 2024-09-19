package io.github.posaydone.filmix.tv.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class NavigationDrawerScreens<T>(val name: String, val icon: String, val route: T) {
    @Serializable
    data object Home : NavigationDrawerScreens<Screens.Main.Home>(
        name = "Home",
        icon = "Home",
        route = Screens.Main.Home
    )

    @Serializable
    data object Explore : NavigationDrawerScreens<Screens.Main.Explore>(
        name = "Explore",
        icon = "Explore",
        route = Screens.Main.Explore
    )

    @Serializable
    data object History : NavigationDrawerScreens<Screens.Main.History>(
        name = "Scheme",
        icon = "History",
        route = Screens.Main.History
    )
}
