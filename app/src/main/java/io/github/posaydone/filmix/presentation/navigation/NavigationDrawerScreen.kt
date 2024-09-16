package io.github.posaydone.filmix.presentation.navigation

import io.github.posaydone.filmix.R
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationDrawerScreens<T>(val name: String, val icon: Int, val route: T) {
    @Serializable
    data object Home : BottomScreens<MobileScreens.Main.Home>(
        name = "Home",
        icon = R.drawable.ic_home,
        route = MobileScreens.Main.Home
    )

    @Serializable
    data object History : BottomScreens<MobileScreens.Main.History>(
        name = "Scheme",
        icon = R.drawable.ic_history,
        route = MobileScreens.Main.History
    )
}
