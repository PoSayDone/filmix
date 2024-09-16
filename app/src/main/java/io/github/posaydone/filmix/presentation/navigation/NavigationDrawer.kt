package io.github.posaydone.filmix.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Text


@Composable
fun NavigationDrawer(navController: NavController, content: @Composable () -> Unit) {
    val navigationDrawerScreens = remember {
        listOf(
            NavigationDrawerScreens.Home,
            NavigationDrawerScreens.History,
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
//    val navigationDrawerDestination =
//        navigationDrawerScreens.any { it.route::class.qualifiedName == currentDestination?.route }


    NavigationDrawer(
        drawerContent = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                navigationDrawerScreens.forEach { screen ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true
                    NavigationDrawerItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(screen.icon),
                                contentDescription = screen.name
                            )
                        },
                        label = {
                            Text(
                                text = screen.name,
                            )
                        },
                    )
                }
            }
        },
        content = content
    )
}