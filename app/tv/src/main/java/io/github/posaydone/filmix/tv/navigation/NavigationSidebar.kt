package io.github.posaydone.filmix.tv.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
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
import kotlinx.coroutines.delay

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
            NavigationDrawerScreens.Favorites
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
        else return Icons.Default.Error
    }

    var focusEnabled by remember { mutableStateOf(true) }
    LaunchedEffect(destination) {
        // Keep focus enabled always for navigation sidebar
        focusEnabled = true
    }

    NavigationDrawer(
        drawerState = drawerState, drawerContent = {
            if (destination) LazyColumn(
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxHeight()
                    .focusGroup()
                    .focusRestorer()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(
                    8.dp, alignment = Alignment.CenterVertically
                ),
            ) {
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
            }
        }) {
        content()
    }
}
