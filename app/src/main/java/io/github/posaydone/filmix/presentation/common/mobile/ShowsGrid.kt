package io.github.posaydone.filmix.presentation.common.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.data.entities.Show
import io.github.posaydone.filmix.presentation.navigation.Screens

@Composable
fun ShowsGrid(shows: List<Show>, navController: NavHostController) {
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(shows) { show ->
            ShowCard(show) {
                navController.navigate(Screens.Main.Details(show.id)) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}