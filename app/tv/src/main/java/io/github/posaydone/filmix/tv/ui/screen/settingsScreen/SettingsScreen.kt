@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)

package io.github.posaydone.filmix.tv.ui.screen.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.common.sharedViewModel.SettingsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.SettingsScreenViewModel
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.SideDialog
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SettingsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is SettingsScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize(), onRetry = state.onRetry)
        }

        is SettingsScreenUiState.Success -> {
            SettingsScreenContent(
                currentVideoQuality = videoQuality,
                currentStreamType = state.currentStreamType,
                currentServerLocation = state.currentServerLocation,
                onStreamTypeChange = { viewModel.updateStreamType(it) },
                onServerLocationChange = { viewModel.updateServerLocation(it) },
                onVideoQualityChange = { viewModel.setVideoQuality(it) })
        }
    }
}

@Composable
fun SettingsScreenContent(
    currentVideoQuality: String,
    currentStreamType: String,
    currentServerLocation: String,
    onStreamTypeChange: (String) -> Unit,
    onServerLocationChange: (String) -> Unit,
    onVideoQualityChange: (String) -> Unit
) {
    val videoQualities = SettingsScreenViewModel.videoQualities
    val streamTypes = SettingsScreenViewModel.streamTypes
    val serverLocations = SettingsScreenViewModel.serverLocations

    var showVideoQualityDialog by remember { mutableStateOf(false) }
    var showStreamTypeDialog by remember { mutableStateOf(false) }
    var showServerLocationDialog by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState();
    val childPadding = rememberChildPadding();
    
    LazyColumn(
        state = lazyListState, modifier = Modifier.width(500.dp), contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
            top = childPadding.top,
            bottom = childPadding.bottom
        ), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                modifier = Modifier.padding(
                    top = 24.dp, bottom = 24.dp
                ),
                text = "Settings",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "Player",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            SettingCard(
                title = "Video Quality",
                currentValue = videoQualities[currentVideoQuality] ?: currentVideoQuality,
                onClick = { showVideoQualityDialog = true })
        }

        item {
            SettingCard(
                title = "Stream Type",
                currentValue = streamTypes[currentStreamType] ?: currentStreamType,
                onClick = { showStreamTypeDialog = true })
        }

        item {
            SettingCard(
                title = "Server Location",
                currentValue = serverLocations[currentServerLocation] ?: currentServerLocation,
                onClick = { showServerLocationDialog = true })
        }
    }

    // Video Quality Dialog
    SettingDialog(
        title = "Video quality",
        description = "Choose the default video quality, which will be used in the player",
        currentValue = currentVideoQuality,
        values = videoQualities,
        onValueSelected = { quality ->
            onVideoQualityChange(quality)
            showVideoQualityDialog = false
        },
        opened = showVideoQualityDialog,
        onDismiss = { showVideoQualityDialog = false })

    // Stream Type Dialog
    SettingDialog(
        title = "Stream type",
        description = "Type of video stream, pick auto if unsure",
        currentValue = currentStreamType,
        values = streamTypes,
        onValueSelected = { streamType ->
            onStreamTypeChange(streamType)
            showStreamTypeDialog = false
        },
        opened = showStreamTypeDialog,
        onDismiss = { showStreamTypeDialog = false })

    // Server Location Dialog
    SettingDialog(
        title = "Server location",
        description = "Pick the nearest location for a better speed",
        currentValue = currentServerLocation,
        values = serverLocations,
        onValueSelected = { serverLocation ->
            onServerLocationChange(serverLocation)
            showServerLocationDialog = false
        },
        opened = showServerLocationDialog,
        onDismiss = { showServerLocationDialog = false },
    )
}

@Composable
fun SettingCard(
    title: String,
    currentValue: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        scale = CardDefaults.scale(focusedScale = 1.05f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Open settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingDialog(
    title: String,
    description: String?,
    opened: Boolean,
    values: Map<String, String>,
    currentValue: String,
    onValueSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    SideDialog(
        showDialog = opened, onDismissRequest = onDismiss, title = title, description= description
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(values.toList()) { (key, label) ->
                Card(
                    onClick = { onValueSelected(key) },
                    modifier = Modifier.fillMaxWidth(),
                    scale = CardDefaults.scale(focusedScale = 1.05f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label, style = MaterialTheme.typography.bodyLarge
                        )
                        if (key == currentValue) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}