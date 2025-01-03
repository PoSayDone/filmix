@file:kotlin.OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.posaydone.filmix.mobile.ui.screen.playerScreen

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import io.github.posaydone.filmix.core.model.Translation
import io.github.posaydone.filmix.core.model.VideoWithQualities
import io.github.posaydone.filmix.mobile.ui.utils.toHhMmSs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    viewModel.saveProgress()
                    viewModel.player.stop()
                    insetsController.apply {
                        show(WindowInsetsCompat.Type.systemBars())
                    }
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
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
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                scale *= zoom
                viewModel.setResizeMode(if (scale > 1) AspectRatioFrameLayout.RESIZE_MODE_FIT else AspectRatioFrameLayout.RESIZE_MODE_FIT)
            }
        }
        .focusable()
    )
    {
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
        }, modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom > 1) {
                        viewModel.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                    } else if (zoom < 1) {
                        viewModel.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    }
                }
            }
            .combinedClickable(indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { showControls = !showControls },
                onDoubleClick = { viewModel.player.seekForward() })
        )

        AnimatedVisibility(
            showControls, enter = fadeIn(), exit = fadeOut()
        ) {
            MiddleControls(
                isPlaying = playerState.isPlaying,
                onPlayPauseClick = {
                    viewModel.onPlayPauseClick()
                },
            )
        }

        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopControls(showTitle = details?.title ?: "",
                    onMoreClick = { isQualitySheetOpen = true })
                Spacer(modifier = Modifier.weight(1f))
                BottomControls(
                    viewModel.player,
                    showType = showType,
                    onAudioClick = { isAudioSheetOpen = true },
                    onEpisodeClick = { isEpisodeBottomSheetOpen = true },
                )
            }
        }
    }
//    DisposableEffect(key1 = Unit) {
//        onDispose {
//            viewModel.saveProgress()
//            viewModel.player.release()
//            insetsController.apply {
//                show(WindowInsetsCompat.Type.systemBars())
//            }
//        }
//    }
}


@Composable
private fun MiddleControls(
    isPlaying: Boolean, onPlayPauseClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(64.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .focusable(),
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "Pause",
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun TopControls(
    showTitle: String,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = showTitle,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.focusable(),
            onClick = onMoreClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more),
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
    showType: ShowType?,
    onAudioClick: () -> Unit,
    onEpisodeClick: () -> Unit,
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
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        if (totalDuration > 0L) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(text = currentTime.toHhMmSs(), color = Color.White)
                Text(text = "/", color = Color.White)
                Text(text = totalDuration.toHhMmSs(), color = Color.White)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showType == ShowType.SERIES) {
                OutlinedButton(
                    modifier = Modifier.focusable(),
                    onClick = onEpisodeClick, colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        stringResource(R.string.episodesString)
                    )
                }
            }
            OutlinedButton(
                modifier = Modifier.focusable(),
                onClick = onAudioClick, colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    stringResource(R.string.audioString)
                )
            }
        }
    }
    Row(
        modifier = Modifier
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomSeekBar(
            player = player,
            isSeekInProgress = { isInProgress ->
                isSeekInProgress = isInProgress
            },
            onSeekBarMove = { position ->
                currentTime = position
            },
            totalDuration = totalDuration,
            currentTime = currentTime,
        )
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

        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent
        ) {
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
    player: Player,
    isSeekInProgress: (Boolean) -> Unit,
    onSeekBarMove: (Long) -> Unit,
    currentTime: Long,
    totalDuration: Long,
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
                    player.seekTo(previousScrubPosition)
                } else {
                    player.seekTo(position)
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
            setPosition(player.currentPosition)
        }
    }, update = {
        it.apply {
            setPosition(currentTime)
        }
    },

        modifier = modifier
    )
}