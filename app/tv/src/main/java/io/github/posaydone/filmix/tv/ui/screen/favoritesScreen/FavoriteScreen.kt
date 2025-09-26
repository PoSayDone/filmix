package io.github.posaydone.filmix.tv.ui.screen.favoritesScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.ShowsRow
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoritesScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is FavoritesScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is FavoritesScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize(), onRetry = s.onRetry)
        }

        is FavoritesScreenUiState.Done -> {
            FavoritesScreenContent(
                navController, favoritesList = s.favoritesList, historyList = s.historyList
            )
        }
    }
}

@Composable
fun FavoritesScreenContent(
    navController: NavHostController,
    favoritesList: ShowList,
    historyList: ShowList,
) {
    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                modifier = Modifier
                    .padding(
                        top = 24.dp + childPadding.top,
                        bottom = 24.dp,
                        start = childPadding.start
                    ),
                text = "Favorites",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            ShowsRow(
                title = "Favorite",
                requestInitialFocus = true,
                modifier = Modifier,
                showList = favoritesList,
                onShowSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
        item {
            ShowsRow(
                title = "History",
                modifier = Modifier
                    .padding(
                        bottom = childPadding.bottom
                    ),
                showList = historyList,
                onShowSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}