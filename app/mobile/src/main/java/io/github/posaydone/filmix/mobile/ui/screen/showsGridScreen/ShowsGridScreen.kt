package io.github.posaydone.filmix.mobile.ui.screen.showsGridScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowsGridQueryType
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowsGridScreenViewModel
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowsGridUiState
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.Loading
import io.github.posaydone.filmix.mobile.ui.common.ShowCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowsGridScreen(
    navController: NavHostController,
    viewModel: ShowsGridScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentQueryType by viewModel.currentQueryType.collectAsStateWithLifecycle()

    val title =
        if (currentQueryType == ShowsGridQueryType.FAVORITES) "Favorite Shows" else "History"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is ShowsGridUiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is ShowsGridUiState.Error -> {
                Error(
                    modifier = Modifier.fillMaxSize(),
                    //                message = state.message,
                    onRetry = { /* Handle retry */ })
            }

            is ShowsGridUiState.Success -> {
                ShowsGridContent(
                    navController = navController,
                    shows = state.shows,
                    hasNextPage = state.hasNextPage,
                    onLoadNext = { viewModel.loadNextPage() },
                    queryType = currentQueryType,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ShowsGridContent(
    navController: NavHostController,
    shows: io.github.posaydone.filmix.core.model.ShowList,
    hasNextPage: Boolean,
    onLoadNext: () -> Unit,
    queryType: ShowsGridQueryType,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3), // Mobile typically has fewer columns
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Show items (no header since it's in the app bar now)
        items(shows) { show ->
            ShowCard(
                show = show, onClick = {
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }, modifier = Modifier.fillMaxWidth()
            )
        }

        if (hasNextPage) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem =
                gridState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore, hasNextPage) {
        if (shouldLoadMore && hasNextPage) {
            onLoadNext()
        }
    }
}