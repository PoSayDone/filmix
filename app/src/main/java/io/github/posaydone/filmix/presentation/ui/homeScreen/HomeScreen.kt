package io.github.posaydone.filmix.presentation.ui.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.presentation.common.FilmixViewModelFactory
import io.github.posaydone.filmix.presentation.common.mobile.ShowsRow

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: HomeScreenViewModel = viewModel<HomeScreenViewModel>(factory = FilmixViewModelFactory),
) {
    val lastSeenShows by viewModel.lastSeenShows.collectAsStateWithLifecycle()
    val viewingShows by viewModel.viewingShows.collectAsStateWithLifecycle()
    val popularMovies by viewModel.popularMovies.collectAsStateWithLifecycle()
    val popularSeries by viewModel.popularSeries.collectAsStateWithLifecycle()
    val popularCartoons by viewModel.popularCartoons.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (error) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.retry_error),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(onClick = { viewModel.loadCategories() }) { Text(stringResource(R.string.retry)) }
            }

        } else {
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
}

