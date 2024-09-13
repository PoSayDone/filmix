package io.github.posaydone.filmix.presentation.uiTv.showDetailsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.tv.material3.AssistChip
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.presentation.navigation.Screens
import io.github.posaydone.filmix.presentation.ui.showDetailsScreen.ShowDetailsScreenViewModel

object DetailsScreenArgs {
    const val movieId = "movieId"
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ShowDetailsScreen(
    showId: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailsScreenViewModel = viewModel<ShowDetailsScreenViewModel>(
        factory = ShowDetailsScreenViewModel.Factory
    ),
) {
    val context = LocalContext.current

    val showDetails by viewModel.showDetails.collectAsState()
    val showHistory by viewModel.showHistory.collectAsState()

    AnimatedVisibility(
        visible = showDetails != null, enter = fadeIn(), exit = fadeOut()
    ) {
        showDetails?.let { show ->
            Box(modifier = modifier.fillMaxSize()) {
                val brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface, Color.Transparent
                    )
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                        .data(show.poster).build(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = show.title,
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush)
                        .padding(32.dp)
                ) {
                    Text(
                        text = show.originalTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = show.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp
                        ),
                    ) {
                        AssistChip(
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(id = R.drawable.ic_imdb),
                                    contentDescription = "Imdb icon"
                                )
                            },
                            onClick = {},
                            modifier = Modifier.focusProperties { canFocus = false },
                        ) {
                            androidx.compose.material3.Text(
                                stringResource(
                                    R.string.score, show.ratingImdb
                                )
                            )
                        }
                        AssistChip(modifier = Modifier.focusProperties { canFocus = false },
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(id = R.drawable.ic_kp),
                                    contentDescription = "Kinopoisk icon"
                                )
                            },
                            onClick = {}) {
                            androidx.compose.material3.Text(
                                stringResource(
                                    R.string.score, show.ratingKinopoisk
                                )
                            )
                        }
                        val ratingFilmix: Double = ((show.votesPos.toDouble()
                            .div((show.votesNeg + show.votesPos).toDouble())) * 10)
                        AssistChip(
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(id = R.drawable.ic_filmix),
                                    contentDescription = "Filmix icon"
                                )
                            },
                            onClick = {},
                            modifier = Modifier.focusProperties { canFocus = false },
                        ) {
                            androidx.compose.material3.Text(
                                stringResource(R.string.score, ratingFilmix)
                            )
                        }
                    }
                    Text(
                        text = show.shortStory,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(0.75f),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Button(modifier = Modifier.padding(top = 24.dp), onClick = {
                        navController.navigate(Screens.Player(showId)) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Play"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showHistory.isNotEmpty()) {
                                if (show.lastEpisode == null) {
                                    stringResource(R.string.continueWatchingMovie)
                                } else {
                                    stringResource(
                                        R.string.continueWatchingSeries,
                                        showHistory[0]!!.season,
                                        showHistory[0]!!.episode
                                    )
                                }
                            } else {
                                stringResource(R.string.playString)
                            }
                        )
                    }
                }
            }
        }
    }
}