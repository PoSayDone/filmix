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
    data object Favorites : NavigationDrawerScreens<Screens.Main.Favorite>(
        name = "Favorite",
        icon = "Favorite",
        route = Screens.Main.Favorite
    )

    @Serializable
    data object Profile : NavigationDrawerScreens<Screens.Main.Profile>(
        name = "Profile",
        icon = "Profile",
        route = Screens.Main.Profile
    )
}
