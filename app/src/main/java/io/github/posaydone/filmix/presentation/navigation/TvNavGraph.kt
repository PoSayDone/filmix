package io.github.posaydone.filmix.presentation.navigation

import androidx.annotation.OptIn
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
import io.github.posaydone.filmix.presentation.uiTv.homeScreen.HomeScreen
import io.github.posaydone.filmix.presentation.uiTv.playerScreen.VideoPlayerScreen
import io.github.posaydone.filmix.presentation.uiTv.showDetailsScreen.ShowDetailsScreen

@Composable
fun TvNavGraph(
) {
    val navController = rememberNavController()

//    NavigationDrawer(navController) {
    NavHost(
        navController = navController,
        startDestination = MobileScreens.Auth
    ) {
        authGraph(navController)
        mainGraph(navController)
        playerGraph(navController)
    }
//    }
}


private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
) {
    composable<MobileScreens.Auth> {
        AuthScreen(navController)
    }
}


private fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
) {
    navigation<MobileScreens.Main>(startDestination = MobileScreens.Main.Home) {
        composable<MobileScreens.Main.Home> {
            HomeScreen(navController = navController)
        }
        composable<MobileScreens.Main.Details> {
            val args = it.toRoute<MobileScreens.Main.Details>()
            ShowDetailsScreen(args.showId, navController)
        }
    }
}

@OptIn(UnstableApi::class)
private fun NavGraphBuilder.playerGraph(
    navController: NavHostController,
) {
    composable<MobileScreens.Player> {
        val args = it.toRoute<MobileScreens.Player>()
        VideoPlayerScreen(args.showId)
    }
}

