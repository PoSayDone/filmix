package io.github.posaydone.filmix.mobile.ui.screen.homeScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.HomeScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.HomeScreenViewModel
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.Loading
import io.github.posaydone.filmix.mobile.ui.common.ShowsRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


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
            Column {
                Error(modifier = Modifier.fillMaxSize(), onRetry = s.onRetry, children = {
                    Button(onClick = {
                        s.sessionManager.saveAccessToken(
                            s.sessionManager.fetchAccessToken(), System.currentTimeMillis() - 1000
                        )
                    }) {
                        Text("clear expiration time")
                    }
                    Button(onClick = {
                        s.sessionManager.saveAccessToken(
                            null, System.currentTimeMillis() - 1000
                        )
                    }) {
                        Text("remove token")
                    }
                    Button(onClick = {
                        s.sessionManager.saveAccessToken(
                            "adsfjskjdfkaksjf", System.currentTimeMillis() + 10 * 60 * 1000
                        )
                    }) {
                        Text("save wrong token")
                    }
                })
            }
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
                sessionManager = s.sessionManager,
                reload = { viewModel.retry() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    sessionManager: SessionManager,
    reload: () -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(state = refreshState, isRefreshing = isRefreshing, onRefresh = {
        coroutineScope.launch {
            isRefreshing = true
            reload()
            delay(1.seconds)
            isRefreshing = false
        }
    }) {

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
//            Button(onClick = {
//                sessionManager.saveAccessToken(
//                    sessionManager.fetchAccessToken(), System.currentTimeMillis() - 1000
//                )
//            }) {
//                Text("clear expiration time")
//            }
//            Button(onClick = {
//                sessionManager.saveAccessToken(
//                    null, System.currentTimeMillis() - 1000
//                )
//            }) {
//                Text("remove token")
//            }
//            Button(onClick = {
//                sessionManager.saveAccessToken(
//                    "adsfjskjdfkaksjf", System.currentTimeMillis() + 10 * 60 * 1000
//                )
//            }) {
//                Text("save wrong token")
//            }
            ShowsRow(
                showList = lastSeenShows,
                title = stringResource(R.string.continue_watching),
                onShowClick = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            ShowsRow(
                showList = viewingShows,
                title = stringResource(R.string.watching_now),
                onShowClick = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            ShowsRow(
                showList = popularMovies,
                title = stringResource(R.string.popular_movies),
                onShowClick = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            ShowsRow(
                showList = popularSeries,
                title = stringResource(R.string.popular_series),
                onShowClick = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            ShowsRow(
                showList = popularCartoons,
                title = stringResource(R.string.popular_cartoons),
                onShowClick = { show ->
                    navController.navigate(Screens.Main.Details(show.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

