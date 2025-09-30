package io.github.posaydone.filmix.mobile.newNavigation.graph

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.posaydone.filmix.mobile.newNavigation.screen.MainScreen
import io.github.posaydone.filmix.mobile.newNavigation.screen.MainScreenSaver
import io.github.posaydone.filmix.mobile.newNavigation.screen.mainScreenItems
import io.github.posaydone.filmix.mobile.ui.screen.exploreScreen.ExploreScreen
import io.github.posaydone.filmix.mobile.ui.screen.favoritesScreen.FavoritesScreen
import io.github.posaydone.filmix.mobile.ui.screen.homeScreen.HomeScreen
import io.github.posaydone.filmix.mobile.ui.screen.profileScreen.ProfileScreen
import io.github.posaydone.filmix.mobile.ui.screen.settingsScreen.SettingsScreen

fun getIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Home" -> Icons.Default.Home
        "Explore" -> Icons.Default.Explore
        "Favorite" -> Icons.Default.Favorite
        "Profile" -> Icons.Default.Person
        "Settings" -> Icons.Default.Settings
        else -> Icons.Default.Error
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGraph() {
    val backStack = rememberNavBackStack<MainScreen>(MainScreen.Home)
    var currentMainScreen: MainScreen by rememberSaveable(
        stateSaver = MainScreenSaver
    ) {
        mutableStateOf(MainScreen.Home)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Swag") })
    }, bottomBar = {
        NavigationBar {
            mainScreenItems.forEach { destination ->
                NavigationBarItem(selected = currentMainScreen == destination, icon = {
                    Icon(
                        imageVector = getIcon(destination.icon),
                        contentDescription = "$destination icon"
                    )
                }, onClick = {
                    if (backStack.lastOrNull() != destination) {
                        if (backStack.lastOrNull() in mainScreenItems) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }

                    backStack.add(destination)
                    currentMainScreen = destination
                })
            }
        }
    }) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<MainScreen.Home> {
                    HomeScreen()
                }
                entry<MainScreen.Explore> {
                    ExploreScreen()
                }
                entry<MainScreen.Favorite> {
                    FavoritesScreen()
                }
                entry<MainScreen.Profile> {
                    ProfileScreen()
                }
                entry<MainScreen.Settings> {
                    SettingsScreen()
                }
            })
    }
}
