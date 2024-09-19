package io.github.posaydone.filmix.mobile.ui.screen.historyScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.sharedViewModel.HistoryScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.HistoryScreenViewModel
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.HistoryCard
import io.github.posaydone.filmix.mobile.ui.common.Loading

@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: HistoryScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HistoryScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is HistoryScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is HistoryScreenUiState.Done -> {
            HistoryScreen(
                paddingValues, navController, s.historyList
            )
        }
    }
}

@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    navController: NavHostController,
    historyList: List<ShowDetails>,
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(historyList) { item ->
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