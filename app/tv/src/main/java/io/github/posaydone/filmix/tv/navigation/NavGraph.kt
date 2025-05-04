package io.github.posaydone.filmix.tv.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import io.github.posaydone.filmix.tv.ui.screen.authScreen.AuthScreen
import io.github.posaydone.filmix.tv.ui.screen.exploreScreen.ExploreScreen
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.HomeScreen
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.VideoPlayerScreen
import io.github.posaydone.filmix.tv.ui.screen.showDetailsScreen.ShowDetailsScreen
import io.github.posaydone.filmix.tv.utils.LocalFocusTransferredOnLaunchProvider

@Composable
fun NavGraph(
) {
    val navController = rememberNavController()

    NavigationSidebar(navController = navController) {
        NavHost(
            navController = navController, startDestination = Screens.Auth
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


