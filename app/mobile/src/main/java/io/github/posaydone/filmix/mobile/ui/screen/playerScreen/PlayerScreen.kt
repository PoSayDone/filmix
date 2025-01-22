@file:kotlin.OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.posaydone.filmix.mobile.ui.screen.playerScreen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.AutoAwesomeMotion
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.PlayerScreenViewModel
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowType
import io.github.posaydone.filmix.core.model.Episode
import io.github.posaydone.filmix.core.model.File
import io.github.posaydone.filmix.core.model.Season
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.Translation
import io.github.posaydone.filmix.core.model.VideoWithQualities
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.components.PlayerMediaTitle
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.components.PlayerPulse
import io.github.posaydone.filmix.mobile.ui.screen.playerScreen.components.rememberPlayerPulseState
import io.github.posaydone.filmix.mobile.ui.utils.toHhMmSs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@UnstableApi
@Composable
fun PlayerScreen(
    showId: Int,
    viewModel: PlayerScreenViewModel = hiltViewModel(),
) {
    val playerState by viewModel.playerState.collectAsState()
    val details by viewModel.details.collectAsState()
    val seasons by viewModel.seasons.collectAsState()
    val moviePieces by viewModel.moviePieces.collectAsState()
    val selectedEpisode by viewModel.selectedEpisode.collectAsState()
    val selectedSeason by viewModel.selectedSeason.collectAsState()
    val selectedTranslation by viewModel.selectedTranslation.collectAsState()
    val selectedMovieTranslation by viewModel.selectedMovieTranslation.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val showType by viewModel.contentType.collectAsState()
    var scale by remember { mutableStateOf(1f) }

    val hasPrevEpisode by viewModel.hasPrevEpisode.collectAsState()
    val hasNextEpisode by viewModel.hasNextEpisode.collectAsState()

    var showControls by rememberSaveable {
        mutableStateOf(false)
    }
    var isAudioSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isQualitySheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isEpisodeBottomSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }


    val view = LocalView.current
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val pulseState = rememberPlayerPulseState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Log.d("Lifecycle", event.name)
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.saveProgress()
                    viewModel.pause()
                    insetsController.apply {
                        show(WindowInsetsCompat.Type.systemBars())
                    }
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            viewModel.saveProgress()
            viewModel.player.stop()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    if (!view.isInEditMode) {
        if (!showControls) {
            insetsController.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            insetsController.apply {
                show(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    LaunchedEffect(key1 = showControls) {
        if (showControls) {
            delay(10000)
            showControls = false
        }
    }


    if (showType == ShowType.SERIES) {
        if (seasons != null && selectedSeason != null) {
            EpisodeBottomSheet(viewModel = viewModel,
                seasons = seasons!!,
                selectedSeason = selectedSeason,
                selectedEpisode = selectedEpisode,
                isEpisodeBottomSheetOpen = isEpisodeBottomSheetOpen,
                onDismiss = { isEpisodeBottomSheetOpen = false })
        }

        selectedEpisode?.translations?.let { translations ->
            AudioBottomSheet(
                translations, selectedTranslation, viewModel, isAudioSheetOpen, showType
            ) {
                isAudioSheetOpen = false
            }
        }
        selectedTranslation?.files?.let { qualities ->
            QualityBottomSheet(qualities, selectedQuality, viewModel, isQualitySheetOpen) {
                isQualitySheetOpen = false
            }
        }
    } else {
        moviePieces?.let { translations ->
            AudioBottomSheet(
                translations, selectedMovieTranslation, viewModel, isAudioSheetOpen, showType
            ) {
                isAudioSheetOpen = false
            }
        }
        selectedMovieTranslation?.files?.let { qualities ->
            QualityBottomSheet(qualities, selectedQuality, viewModel, isQualitySheetOpen) {
                isQualitySheetOpen = false
            }
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .focusable()
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                if (zoom > 1) {
                    viewModel.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                } else if (zoom < 1) {
                    viewModel.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                }
            }
        }) {
        AndroidView(factory = {
            PlayerView(it).apply {
                player = viewModel.player
                useController = false
                resizeMode = playerState.resizeMode
                keepScreenOn = playerState.isPlaying
            }
        }, update = {
            it.apply {
                resizeMode = playerState.resizeMode
                keepScreenOn = playerState.isPlaying
            }
        }, modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Left side for seeking backward
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        showControls = !showControls
                    }, // Single tap for toggling controls
                        onDoubleTap = {
                            viewModel.player.seekBack()
                            pulseState.setType(PlayerPulse.Type.BACK) // Optional feedback
                        })
                })

            // Right side for seeking forward
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        showControls = !showControls
                    }, // Single tap for toggling controls
                        onDoubleTap = {
                            viewModel.player.seekForward()
                            pulseState.setType(PlayerPulse.Type.FORWARD) // Optional feedback
                        })
                })
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerPulse(pulseState)
        }

        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut()
        ) {
            MiddleControls(
                showType = showType,
                isPlaying = playerState.isPlaying,
                onPlayPauseClick = {
                    viewModel.onPlayPauseClick()
                },
                hasPrevEpisode = hasPrevEpisode,
                hasNextEpisode = hasNextEpisode,
                onPrevEpisodeClick = { viewModel.goToPrevEpisode() },
                onNextEpisodeClick = { viewModel.goToNextEpisode() },
            )
        }

        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopControls(
                    showType = showType,
                    showDetails = details,
                    onMoreClick = { isQualitySheetOpen = true },
                    onAudioClick = { isAudioSheetOpen = true },
                    onEpisodeClick = { isEpisodeBottomSheetOpen = true },
                )
                Spacer(modifier = Modifier.weight(1f))
                BottomControls(player = viewModel.player)
            }
        }
    }
}


@Composable
private fun MiddleControls(
    showType: ShowType?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    hasNextEpisode: Boolean,
    onNextEpisodeClick: () -> Unit,
    hasPrevEpisode: Boolean,
    onPrevEpisodeClick: () -> Unit,
    interactionSource: MutableInteractionSource? = null,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 48.dp, alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showType != ShowType.MOVIE) Box(
            modifier = Modifier.clickable(
                onClick = onPrevEpisodeClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 24.dp)
            ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = "Previous episode",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Box(
            modifier = Modifier.clickable(
                onClick = onPlayPauseClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 32.dp)
            ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "Pause",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        if (showType != ShowType.MOVIE) Box(
            modifier = Modifier.clickable(
                onClick = onNextEpisodeClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 24.dp)
            ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = "Next episode",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun TopControls(
    showType: ShowType?,
    showDetails: ShowDetails?,
    onMoreClick: () -> Unit,
    onAudioClick: () -> Unit,
    onEpisodeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        PlayerMediaTitle(
            title = showDetails?.title ?: "",
            secondaryText = showDetails?.originalTitle ?: "",
            tertiaryText = "",
            modifier = Modifier.weight(1f)
        )
        if (showType == ShowType.SERIES) {
            IconButton(
                modifier = Modifier.focusable(), onClick = onEpisodeClick
            ) {
                Icon(
                    Icons.Rounded.AutoAwesomeMotion,
                    contentDescription = "Pause",
                    tint = Color.White,
                )
            }
        }
        IconButton(
            modifier = Modifier.focusable(), onClick = onAudioClick
        ) {
            Icon(
                Icons.Rounded.Audiotrack,
                contentDescription = "Pause",
                tint = Color.White,
            )
        }
        IconButton(
            modifier = Modifier.focusable(), onClick = onMoreClick
        ) {
            Icon(
                Icons.Rounded.HighQuality,
                contentDescription = "Pause",
                tint = Color.White,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun BottomControls(
    player: Player,
) {
    var currentTime by rememberSaveable {
        mutableLongStateOf(player.currentPosition)
    }

    var totalDuration by rememberSaveable {
        mutableLongStateOf(player.duration)
    }

    var isSeekInProgress by remember {
        mutableStateOf(false)
    }

    val timerCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        timerCoroutineScope.launch {
            while (true) {
                delay(500)
                if (!isSeekInProgress) {
                    currentTime = player.currentPosition
                    totalDuration = player.duration
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (totalDuration > 0L) Box(
            modifier = Modifier.width(72.dp)
        ) {
            Text(
                text = currentTime.toHhMmSs(),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        CustomSeekBar(
            isSeekInProgress = { isInProgress ->
                isSeekInProgress = isInProgress
            },
            onSeekBarMove = { position ->
                currentTime = position
            },
            totalDuration = totalDuration,
            currentTime = currentTime,
            onSeekStop = { position -> player.seekTo(position) },
            modifier = Modifier.weight(1f)
        )
        if (totalDuration > 0L) Box(
            modifier = Modifier.width(72.dp)
        ) {
            Text(
                text = totalDuration.toHhMmSs(),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@UnstableApi
@Composable
private fun EpisodeBottomSheet(
    viewModel: PlayerScreenViewModel,
    seasons: List<Season>,
    selectedSeason: Season?,
    selectedEpisode: Episode?,
    isEpisodeBottomSheetOpen: Boolean,
    onDismiss: () -> Unit,
) {
    if (isEpisodeBottomSheetOpen) ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        var tabIndex by rememberSaveable {
            mutableIntStateOf(
                selectedSeason!!.season.minus(1)
            )
        }

        ScrollableTabRow(selectedTabIndex = tabIndex,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            divider = {}) {
            seasons.forEachIndexed { index, season ->
                Tab(
                    modifier = Modifier.padding(8.dp),
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                ) {
                    Text(stringResource(R.string.season, season.season))
                }
            }
        }

        val selectedSeasonEpisodes = seasons[tabIndex].episodes

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(selectedSeasonEpisodes) { episode ->
                SingleSelectionCard(
                    selectionOption = episode, selectedEpisode
                ) {
                    viewModel.setEpisode(episode)
                }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun <T> AudioBottomSheet(
    translations: List<T>,
    selectedTranslation: T?,
    viewModel: PlayerScreenViewModel,
    isAudioSheetOpen: Boolean,
    showType: ShowType?,
    onDismiss: () -> Unit,
) {

    if (isAudioSheetOpen) ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp)
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
                }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun QualityBottomSheet(
    qualities: List<File>,
    selectedQuality: File?,
    viewModel: PlayerScreenViewModel,
    isQualitySheetOpen: Boolean,
    onDismiss: () -> Unit,
) {

    if (isQualitySheetOpen) ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(qualities) { item ->
                SingleSelectionCard(
                    selectionOption = item,
                    selectedQuality,
                ) {
                    viewModel.setQuality(item)
                }
            }
        }
    }
}


@Composable
fun <T> SingleSelectionCard(selectionOption: T, selectedOption: T?, onOptionClicked: (T) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(32.dp))
            .clickable(true, onClick = { onOptionClicked(selectionOption) }),
        color = if (selectionOption == selectedOption) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = selectionOption.toString(), style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@UnstableApi
@Composable
fun CustomSeekBar(
    currentTime: Long,
    totalDuration: Long,
    isSeekInProgress: (Boolean) -> Unit,
    onSeekBarMove: (Long) -> Unit,
    onSeekStop: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    AndroidView(factory = { context ->
        val listener = object : TimeBar.OnScrubListener {
            var previousScrubPosition = 0L

            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                isSeekInProgress(true)
                previousScrubPosition = position
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                onSeekBarMove(position)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                if (canceled) {
                    onSeekStop(previousScrubPosition)
                } else {
                    onSeekStop(position)
                }
                isSeekInProgress(false)
            }
        }

        DefaultTimeBar(context).apply {
            setScrubberColor(primaryColor.toArgb())
            setPlayedColor(primaryColor.toArgb())
            setUnplayedColor(primaryColor.copy(0.3f).toArgb())
            addListener(listener)
            setDuration(totalDuration)
            setPosition(currentTime)
        }
    }, update = {
        it.setDuration(totalDuration)
        it.setPosition(currentTime)
    }, modifier = modifier)
}
