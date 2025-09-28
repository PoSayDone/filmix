package io.github.posaydone.filmix.tv.ui.screen.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.SimpleDialog
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding
import io.github.posaydone.filmix.tv.ui.screen.settingsScreen.SettingsScreenViewModel.Companion.serverLocationMap

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
    onVideoQualityChange: (String) -> Unit,
) {
    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()
    var showVideoQualityDialog by remember { mutableStateOf(false) }
    var showStreamTypeDialog by remember { mutableStateOf(false) }
    var showServerLocationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth(0.5f),
        contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
            top = childPadding.top,
            bottom = childPadding.bottom
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                currentValue = currentVideoQuality,
                onClick = { showVideoQualityDialog = true })
        }

        item {
            SettingCard(
                title = "Stream Type",
                currentValue = currentStreamType,
                onClick = { showStreamTypeDialog = true })
        }

        item {
            SettingCard(
                title = "Server Location",
                currentValue = serverLocationMap[currentServerLocation] ?: currentServerLocation,
                onClick = { showServerLocationDialog = true })
        }
    }

    // Video Quality Dialog
    if (showVideoQualityDialog) {
        VideoQualityDialog(currentQuality = currentVideoQuality, onQualitySelected = { quality ->
            onVideoQualityChange(quality)
            showVideoQualityDialog = false
        }, onDismiss = { showVideoQualityDialog = false })
    }

    // Stream Type Dialog
    if (showStreamTypeDialog) {
        StreamTypeDialog(
            currentStreamType = currentStreamType,
            onStreamTypeSelected = { streamType ->
                onStreamTypeChange(streamType)
                showStreamTypeDialog = false
            },
            onDismiss = { showStreamTypeDialog = false })
    }

    // Server Location Dialog
    if (showServerLocationDialog) {
        ServerLocationDialog(
            currentServerLocation = currentServerLocation,
            onServerLocationSelected = { serverLocation ->
                onServerLocationChange(serverLocation)
                showServerLocationDialog = false
            },
            onDismiss = { showServerLocationDialog = false })
    }
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
fun VideoQualityDialog(
    currentQuality: String,
    onQualitySelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val qualities = listOf("Auto", "High", "Medium", "Low")

    SimpleDialog(
        onDismissRequest = onDismiss, title = "Video Quality"
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(qualities) { quality ->
                Card(
                    onClick = { onQualitySelected(quality) }, modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quality, style = MaterialTheme.typography.bodyLarge
                        )
                        if (quality == currentQuality) {
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

@Composable
fun StreamTypeDialog(
    currentStreamType: String,
    onStreamTypeSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val streamTypes = listOf("auto", "hls", "mp4")

    SimpleDialog(
        onDismissRequest = onDismiss, title = "Stream Type"
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(streamTypes) { streamType ->
                val displayType = when (streamType) {
                    "auto" -> "Auto"
                    "hls" -> "HLS"
                    "mp4" -> "MP4"
                    else -> streamType
                }

                Card(
                    onClick = { onStreamTypeSelected(streamType) },
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
                            text = displayType, style = MaterialTheme.typography.bodyLarge
                        )
                        if (streamType == currentStreamType) {
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

@Composable
fun ServerLocationDialog(
    currentServerLocation: String,
    onServerLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val serverLocations = serverLocationMap.toList()

    SimpleDialog(
        onDismissRequest = onDismiss, title = "Server Location"
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(serverLocations) { (locationCode, locationName) ->
                Card(
                    onClick = { onServerLocationSelected(locationCode) },
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
                            text = locationName, style = MaterialTheme.typography.bodyLarge
                        )
                        if (locationCode == currentServerLocation) {
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