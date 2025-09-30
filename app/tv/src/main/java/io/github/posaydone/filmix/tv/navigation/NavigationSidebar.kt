package io.github.posaydone.filmix.tv.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemColors
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState

@Composable
fun NavigationSidebar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val items = remember {
        listOf(
            NavigationDrawerScreens.Home,
            NavigationDrawerScreens.Explore,
            NavigationDrawerScreens.Favorites,
            NavigationDrawerScreens.Settings
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val destination = items.any { it.route::class.qualifiedName == currentDestination?.route }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    fun getIcon(iconName: String): ImageVector {
        if (iconName == "Home") return Icons.Default.Home
        if (iconName == "Explore") return Icons.Default.Explore
        if (iconName == "Favorite") return Icons.Default.Favorite
        if (iconName == "Profile") return Icons.Default.Favorite  // Using Favorite as default for Profile
        if (iconName == "Settings") return Icons.Default.Settings
        else return Icons.Default.Error
    }


    NavigationDrawer(
        drawerState = drawerState, drawerContent = {
            if (destination) {
                LazyColumn(
                    userScrollEnabled = false,
                    modifier = Modifier
                        .focusRestorer()
                        .padding(12.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp
                    ),
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxHeight(fraction = 1F / items.count()),
                        ) {
                            NavigationDrawerItem(
                                selected = currentDestination?.hierarchy?.any { it.route == "Profile" } == true,
                                onClick = {
                                    navController.navigate(Screens.Main.Profile)
                                    drawerState.setValue(DrawerValue.Closed)
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = null,
                                    )
                                },
                            ) {
                                Text("Profile")
                            }
                        }
                    }

                    itemsIndexed(items) { index, item ->
                        val text = item.name
                        val icon = getIcon(item.icon)
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true

                        NavigationDrawerItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route)
                                drawerState.setValue(DrawerValue.Closed)
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                )
                            },
                        ) {
                            Text(text)
                        }
                    }


                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(fraction = 1F / 5)
                        )
                    }
                }
            }
        }) {
        content()
    }
}
