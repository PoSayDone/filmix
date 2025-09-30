package io.github.posaydone.filmix.mobile.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(navController: NavController) {
    val bottomScreens = remember {
        listOf(
            BottomScreens.Home,
            BottomScreens.Explore,
            BottomScreens.Favorite,
            BottomScreens.Profile,
            BottomScreens.Settings
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination =
        bottomScreens.any { it.route::class.qualifiedName == currentDestination?.route }

    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            "Home" -> Icons.Default.Home
            "Explore" -> Icons.Default.Explore
            "Favorite" -> Icons.Default.Favorite
            "Profile" -> Icons.Default.Person
            "Settings" -> Icons.Default.Settings
            else -> Icons.Default.Error
        }
    }


    if (bottomBarDestination) {
        NavigationBar(
        ) {
            bottomScreens.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true
                NavigationBarItem(
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
                            imageVector = getIcon(screen.icon), contentDescription = screen.name
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
    }
}
