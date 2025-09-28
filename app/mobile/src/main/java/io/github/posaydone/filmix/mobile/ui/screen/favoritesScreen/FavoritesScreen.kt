package io.github.posaydone.filmix.mobile.ui.screen.favoritesScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.sharedViewModel.FavoritesScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.FavoritesScreenViewModel
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.Loading
import io.github.posaydone.filmix.mobile.ui.common.ShowsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: FavoritesScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val s = uiState) {
            is FavoritesScreenUiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is FavoritesScreenUiState.Error -> {
                Error(modifier = Modifier.fillMaxSize(), onRetry = s.onRetry)
            }

            is FavoritesScreenUiState.Done -> {
                FavoritesScreenContent(
                    paddingValues = paddingValues,
                    navController = navController,
                    favoritesList = s.favoritesList,
                    historyList = s.historyList,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun FavoritesScreenContent(
    paddingValues: PaddingValues,
    navController: NavHostController,
    favoritesList: List<io.github.posaydone.filmix.core.model.Show>,
    historyList: List<io.github.posaydone.filmix.core.model.Show>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp), verticalArrangement = Arrangement.Center
    ) {
        ShowsRow(
            title = "Favorites",
            modifier = Modifier.fillMaxWidth(),
            showList = favoritesList,
            onShowClick = { show ->
                navController.navigate(Screens.Main.Details(show.id)) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onViewAll = {
                navController.navigate(Screens.Main.ShowsGrid("FAVORITES"))
            }
        )
        
        ShowsRow(
            title = "History",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            showList = historyList,
            onShowClick = { show ->
                navController.navigate(Screens.Main.Details(show.id)) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onViewAll = {
                navController.navigate(Screens.Main.ShowsGrid("HISTORY"))
            }
        )
    }
}