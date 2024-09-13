package io.github.posaydone.filmix.presentation.navigation

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.google.jetstream.presentation.screens.videoPlayer.VideoPlayerScreen
import io.github.posaydone.filmix.presentation.ui.authScreen.AuthScreen
import io.github.posaydone.filmix.presentation.uiTv.homeScreen.HomeScreen
import io.github.posaydone.filmix.presentation.uiTv.showDetailsScreen.ShowDetailsScreen

@Composable
fun TvNavGraph(
) {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items =
        listOf(
            "Home" to Icons.Default.Home,
            "Settings" to Icons.Default.Settings,
            "Favourites" to Icons.Default.Favorite,
        )

//    NavigationDrawer(
//        drawerContent = {
//            Column(
//                Modifier
//                    .background(Color.Gray)
//                    .fillMaxHeight()
//                    .padding(12.dp)
//                    .selectableGroup(),
//                horizontalAlignment = Alignment.Start,
//                verticalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                items.forEachIndexed { index, item ->
//                    val (text, icon) = item
//
//                    NavigationDrawerItem(
//                        selected = selectedIndex == index,
//                        onClick = { selectedIndex = index },
//                        leadingContent = {
//                            Icon(
//                                imageVector = icon,
//                                contentDescription = null,
//                            )
//                        }
//                    ) {
//                        Text(text)
//                    }
//                }
//            }
//        }
//    ) {

    NavHost(
        navController = navController,
        startDestination = Screens.Auth
    ) {
        authGraph(navController)
        mainGraph(navController)
        playerGraph(navController)
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
            HomeScreen(navController = navController)
        }
        composable<Screens.Main.Details> {
            val args = it.toRoute<Screens.Main.Details>()
            ShowDetailsScreen(args.showId, navController)
        }
    }
}

@OptIn(UnstableApi::class)
private fun NavGraphBuilder.playerGraph(
    navController: NavHostController,
) {
    composable<Screens.Player> {
        val args = it.toRoute<Screens.Player>()
        VideoPlayerScreen(args.showId)
    }
}

