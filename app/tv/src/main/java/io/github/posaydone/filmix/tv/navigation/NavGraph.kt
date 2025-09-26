package io.github.posaydone.filmix.tv.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import io.github.posaydone.filmix.core.model.AuthEvent
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.tv.ui.screen.authScreen.AuthScreen
import io.github.posaydone.filmix.tv.ui.screen.exploreScreen.ExploreScreen
import io.github.posaydone.filmix.tv.ui.screen.favoritesScreen.FavoritesScreen
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.HomeScreen
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.VideoPlayerScreen
import io.github.posaydone.filmix.tv.ui.screen.showDetailsScreen.ShowDetailsScreen
import io.github.posaydone.filmix.tv.utils.LocalFocusTransferredOnLaunchProvider
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavGraph(
    sessionManager: SessionManager,
    authEventFlow: SharedFlow<@JvmSuppressWildcards AuthEvent>
) {
    val navController = rememberNavController()

    val startDestination = if (sessionManager.isLoggedIn()) Screens.Main else Screens.Auth

    LaunchedEffect(key1 = true) {
        authEventFlow.collectLatest { event ->
            when (event) {
                is AuthEvent.ForceLogout -> {
                    navController.navigate(Screens.Auth) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    NavigationSidebar(navController = navController) {
        NavHost(
            navController = navController, startDestination = startDestination
        ) {
            authGraph(navController)
            mainGraph(navController)
            playerGraph(navController)
        }
    }
}


private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
) {
    composable<Screens.Auth> {
        AuthScreen(navController)
    }
}


private fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
) {
    navigation<Screens.Main>(startDestination = Screens.Main.Home) {
        composable<Screens.Main.Home> {
            BackHandler {
                navController.popBackStack()
            }
            LocalFocusTransferredOnLaunchProvider {
                HomeScreen(
                    navController = navController,
                )
            }
        }
        composable<Screens.Main.Explore> {
            BackHandler {
                navController.popBackStack()
            }
            LocalFocusTransferredOnLaunchProvider {
                ExploreScreen(navController = navController)
            }
        }
        composable<Screens.Main.Details> {
            val args = it.toRoute<Screens.Main.Details>()
            BackHandler {
                navController.popBackStack()
            }
            LocalFocusTransferredOnLaunchProvider {
                ShowDetailsScreen(args.showId, navController)
            }
        }
        composable<Screens.Main.Favorite> {
            BackHandler {
                navController.popBackStack()
            }
            LocalFocusTransferredOnLaunchProvider {
                FavoritesScreen(navController = navController)
            }
        }
    }
}

private fun NavGraphBuilder.playerGraph(
    navController: NavHostController,
) {
    composable<Screens.Player> {
        val args = it.toRoute<Screens.Player>()
        VideoPlayerScreen(args.showId)
    }
}


