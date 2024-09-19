package io.github.posaydone.filmix.tv.ui.screen.homeScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.HomeScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.HomeScreenViewModel
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.ShowsRow
import io.github.posaydone.filmix.tv.ui.utils.Padding

val ParentPadding = PaddingValues(vertical = 16.dp, horizontal = 58.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction) + 8.dp,
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is HomeScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is HomeScreenUiState.Done -> {
            Body(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                lastSeenShows = s.lastSeenShows,
                viewingShows = s.viewingShows,
                popularMovies = s.popularMovies,
                popularSeries = s.popularSeries,
                popularCartoons = s.popularCartoons,
                goToDetails = { showId ->
                    navController.navigate(Screens.Main.Details(showId)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}


@Composable
private fun Body(
    modifier: Modifier = Modifier,
    lastSeenShows: ShowList,
    viewingShows: ShowList,
    popularMovies: ShowList,
    popularSeries: ShowList,
    popularCartoons: ShowList,
    goToDetails: (showId: Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        modifier = modifier,
    ) {
        item(contentType = "LastSeenRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 8.dp),
                showList = lastSeenShows,
                title = stringResource(R.string.last_seen),
                onMovieSelected = { show ->
                    goToDetails(show.id)
                }
            )
        }
        item(contentType = "ViewingRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = viewingShows,
                title = stringResource(R.string.watching_now),
                onMovieSelected = { show ->
                    goToDetails(show.id)
                }
            )
        }
        item(contentType = "PopularMoviesRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularMovies,
                title = stringResource(R.string.popular_movies),
                onMovieSelected = { show ->
                    goToDetails(show.id)
                }
            )
        }
        item(contentType = "PopularSeriesRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularSeries,
                title = stringResource(R.string.popular_series),
                onMovieSelected = { show ->
                    goToDetails(show.id)
                }
            )
        }
        item(contentType = "PopularCartoonsRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularCartoons,
                title = stringResource(R.string.popular_cartoons),
                onMovieSelected = { show ->
                    goToDetails(show.id)
                }
            )
        }
    }
}
