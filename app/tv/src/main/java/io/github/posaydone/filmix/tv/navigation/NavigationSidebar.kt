package io.github.posaydone.filmix.tv.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState

@Composable
fun NavigationSidebar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items = remember {
        listOf(
            NavigationDrawerScreens.Home,
            NavigationDrawerScreens.Explore,
        )
    }

    fun getIcon(iconName: String): ImageVector {
        if (iconName == "Home")
            return Icons.Default.Home
        if (iconName == "Explore")
            return Icons.Default.Explore
        else
            return Icons.Default.Error
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val destination =
        items.any { it.route::class.qualifiedName == currentDestination?.route }


    val closeDrawerWidth = 80.dp
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (destination)
        ModalNavigationDrawer(
            drawerState = drawerState, drawerContent = { drawer ->

                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxHeight()
                        .padding(12.dp)
                        .selectableGroup(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp, alignment = Alignment.CenterVertically
                    ),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEachIndexed { index, item ->
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
                            }
                        ) {
                            Text(text)
                        }
                    }
                }
            }, scrimBrush = Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.surface, Color.Transparent
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = closeDrawerWidth)
            ) {
                content()
            }
        }
    else content()
}
