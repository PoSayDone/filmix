package io.github.posaydone.filmix.mobile.navigation

import androidx.annotation.OptIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import io.github.posaydone.filmix.mobile.ui.screen.authScreen.AuthScreen
import io.github.posaydone.filmix.mobile.ui.screen.exploreScreen.ExploreScreen
import io.github.posaydone.filmix.mobile.ui.screen.historyScreen.HistoryScreen
import io.github.posaydone.filmix.mobile.ui.screen.homeScreen.HomeScreen
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.PlayerScreen
import io.github.posaydone.filmix.mobile.ui.screen.searchResults.SearchResultsScreen
import io.github.posaydone.filmix.mobile.ui.screen.showDetailsScreen.ShowDetailsScreen

@Composable
fun NavGraph(
) {
    val animationDuration = 300
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigation(navController) },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Auth,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(animationDuration)

                ).plus(
                    fadeIn(
                        animationSpec = tween(animationDuration)
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 4 }, animationSpec = tween(animationDuration)
                ).plus(
                    fadeOut(
                        animationSpec = tween(animationDuration)
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 4 }, animationSpec = tween(animationDuration)
                ).plus(
                    fadeIn(
                        animationSpec = tween(animationDuration)
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(animationDuration)
                ).plus(
                    fadeOut(
                        animationSpec = tween(animationDuration)
                    )
                )
            }) {
            authGraph(navController)
            mainGraph(paddingValues, navController)
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
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavHostController,
) {
    navigation<Screens.Main>(startDestination = Screens.Main.Home) {
        composable<Screens.Main.Home> {
            HomeScreen(paddingValues = paddingValues, navController = navController)
        }
        composable<Screens.Main.Explore> {
            ExploreScreen(paddingValues, navController)
        }
        composable<Screens.Main.History> {
            HistoryScreen(paddingValues, navController)
        }
        composable<Screens.Main.Details> {
            val args = it.toRoute<Screens.Main.Details>()
            ShowDetailsScreen(args.showId, paddingValues, navController)
        }
        composable<Screens.Main.SearchResults> {
            val args = it.toRoute<Screens.Main.SearchResults>()
            SearchResultsScreen(args.query, paddingValues, navController)
        }
    }
}

@OptIn(UnstableApi::class)
private fun NavGraphBuilder.playerGraph(
    navController: NavHostController,
) {
    composable<Screens.Player> {
        val args = it.toRoute<Screens.Player>()
        PlayerScreen(args.showId)
    }
}

