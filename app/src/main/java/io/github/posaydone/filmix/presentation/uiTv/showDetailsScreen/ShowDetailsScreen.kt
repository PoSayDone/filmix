@file:OptIn(ExperimentalTvMaterial3Api::class)

package io.github.posaydone.filmix.presentation.uiTv.showDetailsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.AssistChip
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.presentation.common.tv.Error
import io.github.posaydone.filmix.presentation.common.tv.Loading
import io.github.posaydone.filmix.presentation.navigation.MobileScreens
import io.github.posaydone.filmix.presentation.ui.showDetailsScreen.ShowDetailsScreenUiState
import io.github.posaydone.filmix.presentation.ui.showDetailsScreen.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.presentation.uiTv.homeScreen.rememberChildPadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailsScreen(
    showId: Int,
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
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Details(
    showDetails: ShowDetails,
    showProgress: ShowProgress,
    showImages: ShowImages,
    showTrailers: ShowTrailers,
    goToMoviePlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(432.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        ShowWithGradients(
            showDetails = showDetails,
            showImages = showImages,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.55f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                MovieSmallTitle(movieTitle = showDetails.originalTitle)
                MovieLargeTitle(movieTitle = showDetails.title)
                RatingChips(showDetails)
                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    MovieDescription(
                        description = showDetails.shortStory
                    )
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOf(
                            showDetails.mpaa.toString(),
                            showDetails.year.toString(),
                        )
                    )
                }
                WatchButton(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    goToMoviePlayer = goToMoviePlayer
                )
            }
        }
    }
}

@Composable
private fun WatchButton(
    modifier: Modifier = Modifier,
    goToMoviePlayer: () -> Unit,
) {
    Button(
        onClick = goToMoviePlayer,
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.playString),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun RatingChips(show: ShowDetails) {
    val ratingFilmix: Double = ((show.votesPos.toDouble()
        .div((show.votesNeg + show.votesPos).toDouble())) * 10)

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp
        ),
    ) {
        AssistChip(
            modifier = Modifier
                .focusProperties { canFocus = false },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(18.dp),
                    painter = painterResource(id = R.drawable.ic_imdb),
                    contentDescription = "Imdb icon"
                )
            }, onClick = {}) {
            Text(
                stringResource(
                    R.string.score, show.ratingImdb
                )
            )
        }
        AssistChip(
            modifier = Modifier
                .focusProperties { canFocus = false },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(18.dp),
                    painter = painterResource(id = R.drawable.ic_kp),
                    contentDescription = "Kinopoisk icon"
                )
            }, onClick = {}) {

            Text(
                stringResource(
                    R.string.score, show.ratingKinopoisk
                )
            )
        }
        AssistChip(
            modifier = Modifier
                .focusProperties { canFocus = false },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(18.dp),
                    painter = painterResource(id = R.drawable.ic_filmix),
                    contentDescription = "Filmix icon"
                )
            }, onClick = {}) {
            Text(
                stringResource(R.string.score, ratingFilmix)
            )
        }
    }
}

@Composable
private fun MovieDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 4,
        overflow = TextOverflow.Ellipsis

    )
}

@Composable
private fun MovieLargeTitle(movieTitle: String) {
    Text(
        text = movieTitle,
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

@Composable
private fun MovieSmallTitle(movieTitle: String) {
    Text(
        text = movieTitle,
        style = MaterialTheme.typography.headlineMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

@Composable
private fun ShowWithGradients(
    showDetails: ShowDetails,
    showImages: ShowImages,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {

    val imageUrl = showImages.frames[0]?.url ?: showImages.posters[0]?.url ?: showDetails.poster

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
            .crossfade(true).build(),
        contentDescription = "",
//        contentDescription = StringConstants
//            .Composable
//            .ContentDescription
//            .moviePoster(showDetails.title),
        contentScale = ContentScale.Crop,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, gradientColor),
                    startY = 600f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endX = 1000f,
                    startX = 300f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = 500f, y = 500f),
                    end = Offset(x = 1000f, y = 0f)
                )
            )
        }
    )
}
