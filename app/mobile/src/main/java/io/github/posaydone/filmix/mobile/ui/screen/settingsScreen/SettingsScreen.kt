package io.github.posaydone.filmix.mobile.ui.screen.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import io.github.posaydone.filmix.core.common.sharedViewModel.settings.SettingsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.settings.SettingsScreenViewModel
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.Loading
import io.github.posaydone.filmix.mobile.ui.common.settings.SettingItemLink
import io.github.posaydone.filmix.mobile.ui.common.settings.SettingsGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is SettingsScreenUiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is SettingsScreenUiState.Error -> {
                Error(modifier = Modifier.fillMaxSize(), onRetry = state.onRetry)
            }

            is SettingsScreenUiState.Success -> {
                SettingsScreenContent(
                    paddingValues = paddingValues,
                    navController = navController,
                    currentVideoQuality = videoQuality,
                    currentStreamType = state.currentStreamType,
                    currentServerLocation = state.currentServerLocation,
                )
            }
        }
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navController: NavController,
    currentVideoQuality: String,
    currentStreamType: String,
    currentServerLocation: String,
) {
    val videoQualities = SettingsScreenViewModel.videoQualities
    val streamTypes = SettingsScreenViewModel.streamTypes
    val serverLocations = SettingsScreenViewModel.serverLocations

    Column(
        modifier = modifier
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsGroup(
            title = "Player", items = listOf({
                SettingItemLink(
                    title = "Video Quality",
                    currentValue = videoQualities[currentVideoQuality] ?: currentVideoQuality,
                    onClick = {
                        navController.navigate(
                            Screens.Main.Settings.VideoQuality
                        )
                    })
            }, {

                SettingItemLink(
                    title = "Stream Type",
                    currentValue = streamTypes[currentStreamType] ?: currentStreamType,
                    onClick = {
                        navController.navigate(
                            Screens.Main.Settings.StreamType
                        )
                    })
            }, {
                SettingItemLink(
                    title = "Server Location",
                    currentValue = serverLocations[currentServerLocation] ?: currentServerLocation,
                    onClick = {
                        navController.navigate(
                            Screens.Main.Settings.ServerLocation
                        )
                    })
            })
        )
    }
}

