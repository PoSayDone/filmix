@file:OptIn(ExperimentalTvMaterial3Api::class)

package io.github.posaydone.filmix.tv.ui.screen.showDetailsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.core.model.Country
import io.github.posaydone.filmix.core.model.Genre
import io.github.posaydone.filmix.core.model.KinopoiskCountry
import io.github.posaydone.filmix.core.model.KinopoiskGenre
import io.github.posaydone.filmix.core.model.KinopoiskMovie
import io.github.posaydone.filmix.core.model.LastEpisode
import io.github.posaydone.filmix.core.model.MaxEpisode
import io.github.posaydone.filmix.core.model.Person
import io.github.posaydone.filmix.core.model.Rating
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImage
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.core.model.Votes
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.ImmersiveBackground
import io.github.posaydone.filmix.tv.ui.common.ImmersiveDetails
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.gradientOverlay
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
                kinopoiskMovie = s.kinopoiskMovie,
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
    kinopoiskMovie: KinopoiskMovie?,
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

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ImmersiveBackground(
                imageUrl = kinopoiskMovie?.backdrop?.url ?: showImages.frames.firstOrNull()?.url
                ?: showDetails.poster
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .gradientOverlay(MaterialTheme.colorScheme.surface)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = childPadding.start + 48.dp,
                        top = childPadding.top + 24.dp,
                        end = childPadding.end + 48.dp,
                        bottom = childPadding.bottom + 24.dp
                    ), verticalArrangement = Arrangement.SpaceBetween
            ) {
                ImmersiveDetails(
                    logoUrl = kinopoiskMovie?.logo?.url ?: null,
                    title = kinopoiskMovie?.name ?: showDetails.title,
                    description = if (kinopoiskMovie == null) showDetails.shortStory else if (kinopoiskMovie.shortDescription.isNullOrEmpty()) kinopoiskMovie.description else kinopoiskMovie.shortDescription,
                    rating = kinopoiskMovie?.rating ?: Rating(
                        kp = showDetails.ratingKinopoisk,
                        imdb = showDetails.ratingImdb,
                        filmCritics = .0,
                        russianFilmCritics = .0,
                        await = .0
                    ),
                    votes = kinopoiskMovie?.votes ?: Votes(
                        kp = showDetails.votesKinopoisk,
                        imdb = showDetails.votesIMDB,
                        filmCritics = 0,
                        russianFilmCritics = 0,
                        await = 0
                    ),
                    genres = kinopoiskMovie?.genres ?: showDetails.genres.map { g ->
                        KinopoiskGenre(
                            g.name
                        )
                    },
                    countries = kinopoiskMovie?.countries
                        ?: showDetails.countries.map { c -> KinopoiskCountry(c.name) },
                    year = kinopoiskMovie?.year ?: showDetails.year,
                    movieLength = kinopoiskMovie?.movieLength ?: showDetails.duration,
                    seriesLength = kinopoiskMovie?.seriesLength,
                    ageRating = kinopoiskMovie?.ageRating?.toString() ?: showDetails.mpaa ?: ""
                )
                WatchButton(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    }, goToMoviePlayer = goToMoviePlayer
                )
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
                    id = "3", name = "Director One", poster = "https://example.com/director_one.jpg"
                ), Person(
                    id = "4", name = "Director Two", poster = "https://example.com/director_two.jpg"
                )
            ),
            lastEpisode = LastEpisode(
                season = 2, episode = "10", translation = "Dub", date = "2024-01-15"
            ),
            maxEpisode = MaxEpisode(season = 2, episode = 10),
            countries = arrayListOf(
                Country(id = 1, name = "USA"), Country(id = 2, name = "UK")
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
            status = null,
            isFavorite = true,
            isDeferred = false,
            isHdr = true
        ),
        showImages = ShowImages(
            frames = listOf(
                ShowImage(size = 1920, title = "Frame 1", url = "https://example.com/frame1.jpg"),
                ShowImage(size = 1920, title = "Frame 2", url = "https://example.com/frame2.jpg")
            ), posters = listOf(
                ShowImage(size = 1080, title = "Poster 1", url = "https://example.com/poster1.jpg"),
                ShowImage(size = 1080, title = "Poster 2", url = "https://example.com/poster2.jpg")
            )
        ),
        showProgress = null,
        showTrailers = null,
        kinopoiskMovie = null,
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
            .focusRequester(focusRequester)
            .focusable(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = stringResource(R.string.playString), style = MaterialTheme.typography.titleMedium
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
