package io.github.posaydone.filmix.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
) {
    val bottomScreens = remember {
        listOf(
            BottomScreens.Home,
            BottomScreens.Explore,
            BottomScreens.History,
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    AnimatedContent(targetState = currentDestination?.route) { targetState ->
        Column {
            when (targetState) {
                BottomScreens.Home.route::class.qualifiedName -> {}
                BottomScreens.Explore.route::class.qualifiedName -> {}
                BottomScreens.History.route::class.qualifiedName -> TopAppBar({ Text("App") })
                else -> {}
            }
        }
    }
}

