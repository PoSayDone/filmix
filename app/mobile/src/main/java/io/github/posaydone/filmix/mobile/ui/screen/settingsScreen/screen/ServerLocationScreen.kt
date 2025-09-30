package io.github.posaydone.filmix.mobile.ui.screen.settingsScreen.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.posaydone.filmix.core.common.sharedViewModel.SettingsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.SettingsScreenViewModel
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.Loading
import io.github.posaydone.filmix.mobile.ui.screen.settingsScreen.component.SettingScreenContent
import io.github.posaydone.filmix.mobile.ui.screen.settingsScreen.component.SettingScreenTopAppBar

@Composable
fun ServerLocationScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SettingScreenTopAppBar(title = "Server Location", navController)
        }) { paddingValues ->
        when (val state = uiState) {
            is SettingsScreenUiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is SettingsScreenUiState.Error -> {
                Error(modifier = Modifier.fillMaxSize(), onRetry = state.onRetry)
            }

            is SettingsScreenUiState.Success -> {
                SettingScreenContent(
                    paddingValues = paddingValues,
                    values = SettingsScreenViewModel.serverLocations,
                    currentValue = state.currentServerLocation,
                    updateValue = { value -> viewModel.updateServerLocation(value) })
            }
        }
    }
}

