package io.github.posaydone.filmix.mobile.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class BottomScreens<T>(val name: String, val icon: String, val route: T) {
    @Serializable
    data object Home : BottomScreens<Screens.Main.Home>(
        name = "Home", icon = "Home", route = Screens.Main.Home
    )

    @Serializable
    data object Explore : BottomScreens<Screens.Main.Explore>(
        name = "Explore", icon = "Explore", route = Screens.Main.Explore
    )

    @Serializable
    data object History : BottomScreens<Screens.Main.History>(
        name = "History", icon = "History", route = Screens.Main.History
    )

    @Serializable
    data object Favorite : BottomScreens<Screens.Main.Favorite>(
        name = "Favorite", icon = "Favorite", route = Screens.Main.Favorite
    )

    @Serializable
    data object Profile : BottomScreens<Screens.Main.Profile>(
        name = "Profile", icon = "Profile", route = Screens.Main.Profile
    )
}