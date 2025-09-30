package io.github.posaydone.filmix.mobile.newNavigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.posaydone.filmix.mobile.newNavigation.screen.Screen
import io.github.posaydone.filmix.mobile.ui.screen.authScreen.AuthScreen
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.PlayerScreen

@Composable
fun RootGraph() {
    val backStack = rememberNavBackStack<Screen>(Screen.Auth)
    
    NavDisplay(
        backStack=backStack,
        onBack = {backStack.removeLastOrNull()},
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider { 
            entry<Screen.Auth> {
                AuthScreen()
            }
            entry<Screen.MainGraph> {
                MainGraph()
            }
            entry<Screen.Player> { key ->
                PlayerScreen(key.showId)
            }
        }
    )
    
}
