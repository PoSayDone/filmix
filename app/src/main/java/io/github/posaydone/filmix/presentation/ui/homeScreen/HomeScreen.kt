package io.github.posaydone.filmix.presentation.ui.homeScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.presentation.common.mobile.Error
import io.github.posaydone.filmix.presentation.common.mobile.Loading
import io.github.posaydone.filmix.presentation.common.mobile.ShowsRow


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
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
                paddingValues = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                lastSeenShows = s.lastSeenShows,
                viewingShows = s.viewingShows,
                popularMovies = s.popularMovies,
                popularSeries = s.popularSeries,
                popularCartoons = s.popularCartoons,
                navController = navController,
            )
        }
    }
}

@Composable
private fun Body(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    lastSeenShows: ShowList,
    viewingShows: ShowList,
    popularMovies: ShowList,
    popularSeries: ShowList,
    popularCartoons: ShowList,
    navController: NavHostController,
) {

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        ShowsRow(
            lastSeenShows,
            title = stringResource(R.string.last_seen),
            navController = navController
        )
        ShowsRow(
            viewingShows,
            title = stringResource(R.string.watching_now),
            navController = navController
        )
        ShowsRow(
            popularMovies,
            title = stringResource(R.string.popular_movies),
            navController = navController
        )
        ShowsRow(
            popularSeries,
            title = stringResource(R.string.popular_series),
            navController = navController
        )
        ShowsRow(
            popularCartoons,
            title = stringResource(R.string.popular_cartoons),
            navController = navController
        )
    }
}

