@file:kotlin.OptIn(
    ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class
)

package io.github.posaydone.filmix.tv.ui.screen.playerScreen

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AutoAwesomeMotion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.PlayerScreenViewModel
import io.github.posaydone.filmix.core.common.sharedViewModel.PlayerState
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowType
import io.github.posaydone.filmix.core.model.Episode
import io.github.posaydone.filmix.core.model.File
import io.github.posaydone.filmix.core.model.Season
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.Translation
import io.github.posaydone.filmix.core.model.VideoWithQualities
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.PlayerDialog
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerControlsIcon
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerMainFrame
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerMediaTitle
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerMediaTitleType
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerOverlay
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerPulse
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerPulseState
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerSeeker
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.VideoPlayerState
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.rememberVideoPlayerPulseState
import io.github.posaydone.filmix.tv.ui.screen.playerScreen.components.rememberVideoPlayerState
import io.github.posaydone.filmix.tv.ui.utils.handleDPadKeyEvents
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * [Work in progress] A composable screen for playing a video.
 *
 * @param onBackPressed The callback to invoke when the user presses the back button.
 * @param videoPlayerScreenViewModel The view model for the video player screen.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    showId: Int,
    viewModel: PlayerScreenViewModel = hiltViewModel(),
) {
    val showDetails by viewModel.details.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    when (showDetails) {
        null -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        else -> {
            VideoPlayerScreenContent(
                viewModel.player, viewModel, playerState, showDetails!!
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreenContent(
    player: ExoPlayer,
    viewModel: PlayerScreenViewModel,
    playerState: PlayerState,
    showDetails: ShowDetails,
) {
    val showType by viewModel.contentType.collectAsState()

    val hasPrevEpisode by viewModel.hasPrevEpisode.collectAsState()
    val hasNextEpisode by viewModel.hasNextEpisode.collectAsState()

    val context = LocalContext.current

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    viewModel.saveProgress()
                    viewModel.player.pause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.player.stop()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)

    var contentCurrentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying: Boolean by remember { mutableStateOf(player.isPlaying) }

    var isAudioDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isQualityDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isEpisodeDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            contentCurrentPosition = player.currentPosition
            isPlaying = player.isPlaying
        }
    }

    val pulseState = rememberVideoPlayerPulseState()
    val focusRequester = remember { FocusRequester() }

    Box(
        Modifier
            .dPadEvents(
                player, videoPlayerState, pulseState
            )
            .fillMaxSize()
            .background(color = Color.Black)
            .focusable()
    ) {


        AndroidView(
            factory = {
                PlayerView(context).apply { useController = false }
            },
            update = {
                it.player = player
                it.apply {
                    resizeMode = playerState.resizeMode
                    keepScreenOn = playerState.isPlaying
                }
            }, modifier = Modifier.fillMaxSize()
        )


        VideoPlayerOverlay(modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            state = videoPlayerState,
            isPlaying = isPlaying,
            pulseState = pulseState,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = { /* TODO Implement subtitles */ },
            controls = {
                VideoPlayerControls(showDetails,
                    isPlaying,
                    showType,
                    contentCurrentPosition,
                    player,
                    videoPlayerState,
                    focusRequester,
                    changeSizing = {
                        viewModel.setResizeMode(if (playerState.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    },
                    openEpisodeSheet = { isEpisodeDialogOpen = true },
                    openAudioSheet = { isAudioDialogOpen = true },
                    openQualitySheet = { isQualityDialogOpen = true },
                    hasNextEpisode = hasNextEpisode,
                    hasPrevEpisode = hasPrevEpisode,
                    onPrevEpisodeClick = { viewModel.goToPrevEpisode() },
                    onNextEpisodeClick = { viewModel.goToNextEpisode() })
            })


        Dialogs(focusRequester,
            viewModel,
            isEpisodeDialogOpen,
            isAudioDialogOpen,
            isQualityDialogOpen,
            closeEpisodeSheet = { isEpisodeDialogOpen = false },
            closeAudioSheet = { isAudioDialogOpen = false },
            closeQualitySheet = { isQualityDialogOpen = false })

    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerControls(
    showDetails: ShowDetails,
    isPlaying: Boolean,
    showType: ShowType?,
    contentCurrentPosition: Long,
    exoPlayer: ExoPlayer,
    state: VideoPlayerState,
    focusRequester: FocusRequester,
    changeSizing: () -> Unit,
    openEpisodeSheet: () -> Unit,
    openAudioSheet: () -> Unit,
    openQualitySheet: () -> Unit,
    onPrevEpisodeClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    hasNextEpisode: Boolean,
    hasPrevEpisode: Boolean,
) {
    val onPlayPauseToggle = { shouldPlay: Boolean ->
        if (shouldPlay) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }


    VideoPlayerMainFrame(mediaTitle = {
        VideoPlayerMediaTitle(
            title = showDetails.title,
            secondaryText = showDetails.year.toString(),
            tertiaryText = showDetails.directors?.get(0)?.name ?: "",
            type = VideoPlayerMediaTitleType.DEFAULT
        )
    }, mediaActions = {
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showType == ShowType.SERIES) {
                VideoPlayerControlsIcon(
                    icon = Icons.Rounded.AutoAwesomeMotion,
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = "Playlist button",
                    onClick = openEpisodeSheet,
                )
            }
            VideoPlayerControlsIcon(
                modifier = Modifier.padding(start = 12.dp),
                icon = Icons.Default.Audiotrack,
                state = state,
                isPlaying = isPlaying,
                contentDescription = "Captions button",
                onClick = openAudioSheet,
            )
            VideoPlayerControlsIcon(
                modifier = Modifier.padding(start = 12.dp),
                icon = Icons.Default.Crop,
                state = state,
                isPlaying = isPlaying,
                contentDescription = "Video crop button",
                onClick = changeSizing
            )
            VideoPlayerControlsIcon(
                modifier = Modifier.padding(start = 12.dp),
                icon = Icons.Default.Settings,
                state = state,
                isPlaying = isPlaying,
                contentDescription = "Settings button",
                onClick = openQualitySheet,
            )
        }
    }, seeker = {
        VideoPlayerSeeker(
            focusRequester,
            state,
            isPlaying,
            onPlayPauseToggle,
            onPrevEpisodeClick = onPrevEpisodeClick,
            onNextEpisodeClick = onNextEpisodeClick,
            hasPrevEpisode = hasPrevEpisode,
            hasNextEpisode = hasNextEpisode,
            onSeek = { exoPlayer.seekTo(exoPlayer.duration.times(it).toLong()) },
            contentProgress = contentCurrentPosition.milliseconds,
            contentDuration = exoPlayer.duration.milliseconds
        )
    }, more = null
    )
}


@UnstableApi
@Composable
private fun EpisodeDialog(
    focusRequester: FocusRequester,
    viewModel: PlayerScreenViewModel,
    seasons: List<Season>,
    selectedSeason: Season?,
    selectedEpisode: Episode?,
    isEpisodeDialogOpen: Boolean,
    onDismiss: () -> Unit,
) {
    var tabIndex by rememberSaveable {
        mutableIntStateOf(
            selectedSeason!!.season.minus(1)
        )
    }

    PlayerDialog(
        modifier = Modifier.focusRequester(focusRequester),
        showDialog = isEpisodeDialogOpen,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.handleDPadKeyEvents(onLeft = {
                tabIndex = (tabIndex - 1).coerceAtLeast(0)
            }, onRight = {
                tabIndex = (tabIndex + 1).coerceAtMost(seasons.size - 1)
            })
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                modifier = Modifier
                    .focusRestorer()
                    .fillMaxWidth(),
            ) {
                seasons.forEachIndexed { index, season ->
                    key(index) {
                        Tab(modifier = Modifier.padding(8.dp),
//                            colors = TabDefaults.pillIndicatorTabColors(focusedSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                            selected = index == tabIndex,
                            onFocus = { tabIndex = index },
                            onClick = { tabIndex = index }) {
                            Text(stringResource(R.string.season, season.season))
                        }
                    }
                }
            }

            val selectedSeasonEpisodes = seasons[tabIndex].episodes

            LazyColumn(
                modifier = Modifier.focusRequester(focusRequester),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                items(selectedSeasonEpisodes) { episode ->
                    SingleSelectionCard(
                        selectionOption = episode, selectedEpisode
                    ) {
                        viewModel.setSeason(seasons[tabIndex])
                        viewModel.setEpisode(episode)
                        onDismiss()
                    }
                }
            }
        }
    }

}


@ExperimentalTvMaterial3Api
@OptIn(UnstableApi::class)
@Composable
private fun <T> AudioDialog(
    focusRequester: FocusRequester,
    translations: List<T>,
    selectedTranslation: T?,
    viewModel: PlayerScreenViewModel,
    isAudioDialogOpen: Boolean,
    showType: ShowType?,
    onDismiss: () -> Unit,
) {

    PlayerDialog(
        modifier = Modifier.focusRequester(focusRequester),
        showDialog = isAudioDialogOpen,
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.focusRequester(focusRequester),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(translations) { item ->
                SingleSelectionCard(
                    selectionOption = item,
                    selectedTranslation,
                ) {
                    when (showType) {
                        ShowType.MOVIE -> viewModel.setMovieTranslation(item as VideoWithQualities)
                        ShowType.SERIES -> viewModel.setTranslation(item as Translation)
                        null -> {}
                    }
                    onDismiss()
                }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun QualityDialog(
    focusRequester: FocusRequester,
    qualities: List<File>,
    selectedQuality: File?,
    viewModel: PlayerScreenViewModel,
    isQualitySheetOpen: Boolean,
    onDismiss: () -> Unit,
) {

    PlayerDialog(
        modifier = Modifier.focusRequester(focusRequester),
        showDialog = isQualitySheetOpen,
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.focusRequester(focusRequester),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(qualities) { item ->
                SingleSelectionCard(
                    selectionOption = item,
                    selectedQuality,
                ) {
                    viewModel.setQuality(item)
                    onDismiss()
                }
            }
        }
    }
}


@Composable
fun <T> SingleSelectionCard(selectionOption: T, selectedOption: T?, onOptionClicked: (T) -> Unit) {
    ListItem(headlineContent = { Text(text = selectionOption.toString()) },
        selected = false,
        onClick = { onOptionClicked(selectionOption) },
        trailingContent = {
            if (selectedOption == selectionOption) {
                Icon(Icons.Default.Check, contentDescription = "Check")
            }
        })
}

@OptIn(UnstableApi::class)
@Composable
private fun Dialogs(
    focusRequester: FocusRequester,
    viewModel: PlayerScreenViewModel,
    isEpisodeSheetOpen: Boolean,
    isAudioSheetOpen: Boolean,
    isQualitySheetOpen: Boolean,
    closeEpisodeSheet: () -> Unit,
    closeAudioSheet: () -> Unit,
    closeQualitySheet: () -> Unit,
) {
    val seasons by viewModel.seasons.collectAsState()
    val moviePieces by viewModel.moviePieces.collectAsState()
    val selectedEpisode by viewModel.selectedEpisode.collectAsState()
    val selectedSeason by viewModel.selectedSeason.collectAsState()
    val selectedTranslation by viewModel.selectedTranslation.collectAsState()
    val selectedMovieTranslation by viewModel.selectedMovieTranslation.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val showType by viewModel.contentType.collectAsState()

    if (showType == ShowType.SERIES) {
        if (seasons != null && selectedSeason != null) {
            EpisodeDialog(
                focusRequester = focusRequester,
                viewModel = viewModel,
                seasons = seasons!!,
                selectedSeason = selectedSeason,
                selectedEpisode = selectedEpisode,
                isEpisodeDialogOpen = isEpisodeSheetOpen,
                onDismiss = closeEpisodeSheet
            )
        }

        selectedEpisode?.translations?.let { translations ->
            AudioDialog(
                focusRequester = focusRequester,
                translations,
                selectedTranslation,
                viewModel,
                isAudioSheetOpen,
                showType,
                onDismiss = closeAudioSheet
            )
        }
        selectedTranslation?.files?.let { qualities ->
            QualityDialog(
                focusRequester = focusRequester,
                qualities,
                selectedQuality,
                viewModel,
                isQualitySheetOpen,
                onDismiss = closeQualitySheet
            )
        }
    } else {
        moviePieces?.let { translations ->
            AudioDialog(
                focusRequester = focusRequester,
                translations,
                selectedMovieTranslation,
                viewModel,
                isAudioSheetOpen,
                showType,
                onDismiss = closeAudioSheet
            )
        }
        selectedMovieTranslation?.files?.let { qualities ->
            QualityDialog(
                focusRequester = focusRequester,
                qualities,
                selectedQuality,
                viewModel,
                isQualitySheetOpen,
                onDismiss = closeQualitySheet
            )

        }
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState,
): Modifier = this.handleDPadKeyEvents(onLeft = {
    if (!videoPlayerState.controlsVisible) {
        exoPlayer.seekBack()
        pulseState.setType(VideoPlayerPulse.Type.BACK)
    }
},
    onRight = {
        if (!videoPlayerState.controlsVisible) {
            exoPlayer.seekForward()
            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        exoPlayer.pause()
        videoPlayerState.showControls()
    })
