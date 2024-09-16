package io.github.posaydone.filmix.presentation.navigation

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
import io.github.posaydone.filmix.presentation.ui.authScreen.AuthScreen
import io.github.posaydone.filmix.presentation.ui.exploreScreen.ExploreScreen
import io.github.posaydone.filmix.presentation.ui.historyScreen.HistoryScreen
import io.github.posaydone.filmix.presentation.ui.homeScreen.HomeScreen
import io.github.posaydone.filmix.presentation.ui.playerScreen.PlayerScreen
import io.github.posaydone.filmix.presentation.ui.searchResults.SearchResultsScreen
import io.github.posaydone.filmix.presentation.ui.showDetailsScreen.ShowDetailsScreen

@Composable
fun MobileNavGraph(
) {
    val animationDuration = 300
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigation(navController) },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MobileScreens.Auth,
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
    composable<MobileScreens.Auth> {
        AuthScreen(navController)
    }
}

private fun NavGraphBuilder.mainGraph(
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavHostController,
) {
    navigation<MobileScreens.Main>(startDestination = MobileScreens.Main.Home) {
        composable<MobileScreens.Main.Home> {
            HomeScreen(paddingValues = paddingValues, navController = navController)
        }
        composable<MobileScreens.Main.Explore> {
            ExploreScreen(paddingValues, navController)
        }
        composable<MobileScreens.Main.History> {
            HistoryScreen(paddingValues, navController)
        }
        composable<MobileScreens.Main.Details> {
            val args = it.toRoute<MobileScreens.Main.Details>()
            ShowDetailsScreen(args.showId, paddingValues, navController)
        }
        composable<MobileScreens.Main.SearchResults> {
            val args = it.toRoute<MobileScreens.Main.SearchResults>()
            SearchResultsScreen(args.query, paddingValues, navController)
        }
    }
}

@OptIn(UnstableApi::class)
private fun NavGraphBuilder.playerGraph(
    navController: NavHostController,
) {
    composable<MobileScreens.Player> {
        val args = it.toRoute<MobileScreens.Player>()
        PlayerScreen(args.showId)
    }
}

