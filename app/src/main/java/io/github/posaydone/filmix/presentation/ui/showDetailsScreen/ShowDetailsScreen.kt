@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.posaydone.filmix.presentation.ui.showDetailsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.presentation.common.mobile.Error
import io.github.posaydone.filmix.presentation.common.mobile.Loading
import io.github.posaydone.filmix.presentation.navigation.MobileScreens

val TAG = "ShowDetailsScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailsScreen(
    showId: Int,
    paddingValues: PaddingValues,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is ShowDetailsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is ShowDetailsScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is ShowDetailsScreenUiState.Done -> {
            Details(
                paddingValues = paddingValues,
                showDetails = s.showDetails,
                showProgress = s.showProgress,
                showImages = s.showImages,
                showTrailers = s.showTrailers,
                goToMoviePlayer = {
                    navController.navigate(MobileScreens.Player(showId)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                goBack = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}


@Composable
private fun Details(
    paddingValues: PaddingValues,
    showDetails: ShowDetails,
    showProgress: ShowProgress,
    showImages: ShowImages,
    showTrailers: ShowTrailers,
    goToMoviePlayer: () -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        startY = 0.0f,
        endY = 1000.0f
    )

    Box {
        Column(
            modifier = modifier
                .padding(
                    top = 0.dp,
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = paddingValues.calculateBottomPadding(),
                )
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(500.dp)) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                        .data(showDetails.poster).build(),
                    contentDescription = "Poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .height(430.dp)
                        .fillMaxWidth()
                        .background(gradient)
                        .align(Alignment.BottomCenter),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        text = showDetails.originalTitle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = showDetails.title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 16.dp, alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Text(text = showDetails.year.toString())
                        Text(text = showDetails.countries.get(0).name)
                        showDetails.mpaa?.let {
                            if (it.isNotBlank()) {
                                var text = it
                                if (!text.contains("+")) {
                                    text += "+"
                                }
                                Text(text = text)
                            }
                        }
                    }

                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = goToMoviePlayer, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Play"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showProgress.isNotEmpty()) {
                            if (showDetails.lastEpisode == null) {
                                stringResource(R.string.continueWatchingMovie)
                            } else {
                                stringResource(
                                    R.string.continueWatchingSeries,
                                    showProgress[0].season,
                                    showProgress[0].episode
                                )
                            }
                        } else {
                            stringResource(R.string.playString)
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp, alignment = Alignment.CenterHorizontally
                    ),
                ) {
                    AssistChip(label = {
                        Text(
                            stringResource(
                                R.string.score, showDetails.ratingImdb
                            )
                        )
                    }, leadingIcon = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_imdb),
                            contentDescription = "Imdb icon"
                        )
                    }, onClick = {})
                    AssistChip(label = {
                        Text(

                            stringResource(
                                R.string.score, showDetails.ratingKinopoisk
                            )
                        )
                    }, leadingIcon = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_kp),
                            contentDescription = "Kinopoisk icon"
                        )
                    }, onClick = {})
                    val ratingFilmix: Double = ((showDetails.votesPos.toDouble()
                        .div((showDetails.votesNeg + showDetails.votesPos).toDouble())) * 10)
                    AssistChip(label = {
                        Text(
                            stringResource(R.string.score, ratingFilmix)
                        )
                    }, leadingIcon = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_filmix),
                            contentDescription = "Filmix icon"
                        )
                    }, onClick = {})
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { /* Handle like */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_like),
                            contentDescription = "Like icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = showDetails.votesPos.toString())
                    }

                    OutlinedButton(
                        onClick = { /* Handle dislike */ }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_dislike),
                            contentDescription = "Dislike"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = showDetails.votesNeg.toString())
                    }
                }
                Text(
                    text = showDetails.shortStory,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        TopAppBar({},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = {
                FilledIconButton(
                    onClick = goBack,
                ) {
                    Icon(
                        contentDescription = "Navback icon",
                        painter = painterResource(R.drawable.ic_arrow_back)
                    )
                }
            })
    }
}
