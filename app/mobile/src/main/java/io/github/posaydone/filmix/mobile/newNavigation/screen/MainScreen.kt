package io.github.posaydone.filmix.mobile.newNavigation.screen

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

var mainScreenItems = listOf<MainScreen>(
    MainScreen.Home,
    MainScreen.Explore,
    MainScreen.Favorite,
    MainScreen.Profile,
    MainScreen.Settings,
)

@Serializable
sealed class MainScreen(val icon: String, val title: String): NavKey {
    @Serializable
    data object Home : MainScreen(
        title = "Home", icon = "Home"
    )

    @Serializable
    data object Explore : MainScreen(
        title = "Explore", icon = "Explore"
    )

    @Serializable
    data object Favorite : MainScreen(
        title = "Favorite", icon = "Favorite"
    )

    @Serializable
    data object Profile : MainScreen(
        title = "Profile", icon = "Profile"
    )

    @Serializable
    data object Settings : MainScreen(
        title = "Settings", icon = "Settings"
    )
}

val MainScreenSaver = Saver<MainScreen, String>(
    save = { it::class.simpleName ?: "Unknown"},
    restore = {
        when (it){
            MainScreen.Home::class.simpleName -> MainScreen.Home
            MainScreen.Explore::class.simpleName -> MainScreen.Explore
            MainScreen.Favorite::class.simpleName -> MainScreen.Favorite
            MainScreen.Profile::class.simpleName -> MainScreen.Profile
            MainScreen.Settings::class.simpleName -> MainScreen.Settings
            else -> MainScreen.Home
        }
            
    }
)
