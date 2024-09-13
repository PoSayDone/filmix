package io.github.posaydone.filmix.presentation.uiTv.homeScreen

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.presentation.common.FilmixViewModelFactory
import io.github.posaydone.filmix.presentation.common.tv.ShowsRow
import io.github.posaydone.filmix.presentation.navigation.Screens
import io.github.posaydone.filmix.presentation.ui.homeScreen.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeScreenViewModel = viewModel<HomeScreenViewModel>(factory = FilmixViewModelFactory),
) {
    val lastSeenShows by viewModel.lastSeenShows.collectAsStateWithLifecycle()
    val viewingShows by viewModel.viewingShows.collectAsStateWithLifecycle()
    val popularMovies by viewModel.popularMovies.collectAsStateWithLifecycle()
    val popularSeries by viewModel.popularSeries.collectAsStateWithLifecycle()
    val popularCartoons by viewModel.popularCartoons.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()


    Log.d("HAHA", "HomeScreen: ${lastSeenShows}")

    val lazyListState = rememberLazyListState()


    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp, start = 48.dp, end = 48.dp),
        modifier = modifier,
    ) {
        item(contentType = "LastSeenRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = lastSeenShows,
                title = stringResource(R.string.last_seen),
                onMovieSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        item(contentType = "ViewingRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = viewingShows,
                title = stringResource(R.string.watching_now),
                onMovieSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        item(contentType = "PopularMoviesRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularMovies,
                title = stringResource(R.string.popular_movies),
                onMovieSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        item(contentType = "PopularSeriesRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularSeries,
                title = stringResource(R.string.popular_series),
                onMovieSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        item(contentType = "PopularCartoonsRow") {
            ShowsRow(
                modifier = Modifier.padding(top = 16.dp),
                showList = popularCartoons,
                title = stringResource(R.string.popular_cartoons),
                onMovieSelected = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
