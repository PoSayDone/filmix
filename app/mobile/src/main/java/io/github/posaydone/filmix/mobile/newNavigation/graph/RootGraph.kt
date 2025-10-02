package io.github.posaydone.filmix.mobile.newNavigation.graph

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.posaydone.filmix.core.common.sharedViewModel.PlayerScreenNavKey
import io.github.posaydone.filmix.core.common.sharedViewModel.PlayerScreenViewModel
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsNavKey
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.core.model.AuthEvent
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.mobile.newNavigation.screen.MainGraphData
import io.github.posaydone.filmix.mobile.ui.screen.authScreen.AuthScreen
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.PlayerScreen
import io.github.posaydone.filmix.mobile.ui.screen.showDetailsScreen.ShowDetailsScreen
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(UnstableApi::class)
@Composable
fun RootGraph(
    sessionManager: SessionManager,
    authEventFlow: SharedFlow<@JvmSuppressWildcards AuthEvent>,
) {

    val startDestination =
        if (sessionManager.isLoggedIn()) MainGraphData.MainGraph else MainGraphData.Auth
    val backStack = rememberNavBackStack<MainGraphData>(startDestination)


    LaunchedEffect(key1 = true) {
        authEventFlow.collectLatest { event ->
            when (event) {
                is AuthEvent.ForceLogout -> {
                    backStack.add(MainGraphData.Auth)
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack, onBack = { backStack.removeLastOrNull() }, entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()
        ), entryProvider = entryProvider {
            entry<MainGraphData.Auth> {
                AuthScreen(navigateToHome = { backStack.add(MainGraphData.Auth) })
            }
            entry<MainGraphData.MainGraph> {
                MainGraph()
            }
        })

}
