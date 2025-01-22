@file:OptIn(ExperimentalTvMaterial3Api::class)

package io.github.posaydone.filmix.tv.ui.screen.showDetailsScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.tooling.preview.Preview
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
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.core.model.Country
import io.github.posaydone.filmix.core.model.Genre
import io.github.posaydone.filmix.core.model.LastEpisode
import io.github.posaydone.filmix.core.model.MaxEpisode
import io.github.posaydone.filmix.core.model.Person
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImage
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding
import kotlinx.coroutines.launch

private const val TAG = "ShowDetailsScreen"

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
            Error(modifier = Modifier.fillMaxSize(), onRetry = s.onRetry)
        }

        is ShowDetailsScreenUiState.Done -> {
            Details(
                showDetails = s.showDetails,
                showProgress = s.showProgress,
                showImages = s.showImages,
                showTrailers = s.showTrailers,
                goToMoviePlayer = {
                    navController.navigate(Screens.Player(showId)) {
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
    showProgress: ShowProgress?,
    showImages: ShowImages,
    showTrailers: ShowTrailers?,
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
            showDetails = showDetails, showImages = showImages, modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.55f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                MovieSmallTitle(movieTitle = showDetails.originalTitle)
                MovieLargeTitle(movieTitle = showDetails.title)
                RatingChips(showDetails)
                WatchButton(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    }, goToMoviePlayer = goToMoviePlayer
                )
                Column(
                    modifier = Modifier.alpha(0.75f)

                ) {
                    MovieDescription(
                        description = showDetails.shortStory
                    )
                    Row(
                        modifier = Modifier.padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(36.dp)
                    ) {
                        if (showDetails.mpaa != "" && showDetails.mpaa != null) InfoItem(
                            title = stringResource(R.string.mpaa_rating),
                            value = showDetails.mpaa.toString()
                        )
                        if (showDetails.duration != null)
                            InfoItem(
                                title = stringResource(R.string.duration),
                                value = stringResource(
                                    R.string.movie_duration,
                                    showDetails.duration.toString()
                                )
                            )
                        InfoItem(
                            title = stringResource(R.string.release_year),
                            value = showDetails.year.toString()
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = "id:tv_4k")
@Composable
private fun ShowDetailsScreenPreview() {

    Details(
        showDetails = ShowDetails(
            id = 1,
            category = "TV Show",
            title = "Mock Show",
            originalTitle = "Mock Show Original",
            year = 2023,
            updated = "2024-01-18",
            actors = arrayListOf(
                Person(id = "1", name = "John Doe", poster = "https://example.com/john_doe.jpg"),
                Person(id = "2", name = "Jane Doe", poster = "https://example.com/jane_doe.jpg")
            ),
            directors = arrayListOf(
                Person(
                    id = "3",
                    name = "Director One",
                    poster = "https://example.com/director_one.jpg"
                ),
                Person(
                    id = "4",
                    name = "Director Two",
                    poster = "https://example.com/director_two.jpg"
                )
            ),
            lastEpisode = LastEpisode(
                season = 2,
                episode = "10",
                translation = "Dub",
                date = "2024-01-15"
            ),
            maxEpisode = MaxEpisode(season = 2, episode = 10),
            countries = arrayListOf(
                Country(id = 1, name = "USA"),
                Country(id = 2, name = "UK")
            ),
            genres = arrayListOf(
                Genre(id = 1, name = "Drama", alt_name = "drama"),
                Genre(id = 2, name = "Sci-Fi", alt_name = "sci-fi")
            ),
            poster = "https://example.com/mock_show_poster.jpg",
            rip = "WEBRip",
            quality = "1080p",
            votesPos = 5000,
            votesNeg = 200,
            ratingImdb = 8.5,
            ratingKinopoisk = 8.2,
            url = "https://example.com/mock_show",
            duration = 120,
            votesIMDB = 10000,
            votesKinopoisk = 8000,
            idKinopoisk = 123456,
            mpaa = "PG-13",
            slogan = "A mock show for testing",
            shortStory = "This is a mock show created for testing purposes.",
            status = null, // Assuming ShowStatus has a default constructor
            isFavorite = true,
            isDeferred = false,
            isHdr = true
        ),
        showImages = ShowImages(
            frames = listOf(
                ShowImage(size = 1920, title = "Frame 1", url = "https://example.com/frame1.jpg"),
                ShowImage(size = 1920, title = "Frame 2", url = "https://example.com/frame2.jpg")
            ),
            posters = listOf(
                ShowImage(size = 1080, title = "Poster 1", url = "https://example.com/poster1.jpg"),
                ShowImage(size = 1080, title = "Poster 2", url = "https://example.com/poster2.jpg")
            )
        ),
        showProgress = null,
        showTrailers = null,
        goToMoviePlayer = {},
    )
}

@Composable
private fun InfoItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun InfoItemPreview() {
    InfoItem(title = "Release date", value = "2021")
}

@Composable
private fun WatchButton(
    modifier: Modifier = Modifier,
    goToMoviePlayer: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Button(
        onClick = goToMoviePlayer,
        modifier = modifier
            .padding(top = 24.dp)
            .focusRequester(focusRequester)
            .focusable(),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow, contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.playString), style = MaterialTheme.typography.titleSmall
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun RatingChips(show: ShowDetails) {
    val ratingFilmix: Double =
        ((show.votesPos.toDouble().div((show.votesNeg + show.votesPos).toDouble())) * 10)

    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp
        ),
    ) {
        if (show.ratingImdb != 0.0) AssistChip(modifier = Modifier.focusProperties {
            canFocus = false
        }, leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
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
        if (show.ratingKinopoisk != 0.0) AssistChip(modifier = Modifier.focusProperties {
            canFocus = false
        }, leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
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
        if (ratingFilmix != 0.0) AssistChip(modifier = Modifier.focusProperties {
            canFocus = false
        }, leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
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
        text = description, style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp, fontWeight = FontWeight.Normal
        ), modifier = Modifier.padding(top = 24.dp), maxLines = 4, overflow = TextOverflow.Ellipsis

    )
}

@Composable
private fun MovieLargeTitle(movieTitle: String) {
    Text(
        text = movieTitle, style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold
        ), overflow = TextOverflow.Ellipsis, maxLines = 1
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

    Log.d(TAG, "ShowWithGradients: $showImages")
    val imageUrl = showImages.frames.firstOrNull()?.url ?: showImages.posters.firstOrNull()?.url
    ?: showDetails.poster

    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
        .build(),
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
                    colors = listOf(Color.Transparent, gradientColor), startY = 600f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent), endX = 1000f, startX = 300f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = 500f, y = 500f),
                    end = Offset(x = 1000f, y = 0f)
                )
            )
        })
}
