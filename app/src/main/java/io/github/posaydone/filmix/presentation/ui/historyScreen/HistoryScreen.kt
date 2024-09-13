package io.github.posaydone.filmix.presentation.ui.historyScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.presentation.common.FilmixViewModelFactory
import io.github.posaydone.filmix.presentation.common.mobile.HistoryCard
import io.github.posaydone.filmix.presentation.navigation.Screens

@Composable
fun HistoryScreen(paddingValues: PaddingValues, navController: NavHostController) {

    val viewModel = viewModel<HistoryScreenViewModel>(factory = FilmixViewModelFactory)
    val lastSeenShows = viewModel.historyItemsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(lastSeenShows.value) { item ->
                HistoryCard(item) {
                    navController.navigate(Screens.Main.Details(item.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}