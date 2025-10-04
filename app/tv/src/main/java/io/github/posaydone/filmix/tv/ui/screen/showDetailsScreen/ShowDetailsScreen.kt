@file:OptIn(ExperimentalTvMaterial3Api::class)

package io.github.posaydone.filmix.tv.ui.screen.showDetailsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.core.model.Country
import io.github.posaydone.filmix.core.model.FullShow
import io.github.posaydone.filmix.core.model.Genre
import io.github.posaydone.filmix.core.model.KinopoiskCountry
import io.github.posaydone.filmix.core.model.KinopoiskGenre
import io.github.posaydone.filmix.core.model.LastEpisode
import io.github.posaydone.filmix.core.model.MaxEpisode
import io.github.posaydone.filmix.core.model.KinopoiskPerson
import io.github.posaydone.filmix.core.model.Rating
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImage
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.core.model.Votes
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.ImmersiveBackground
import io.github.posaydone.filmix.tv.ui.common.ImmersiveDetails
import io.github.posaydone.filmix.tv.ui.common.LargeButton
import io.github.posaydone.filmix.tv.ui.common.LargeButtonStyle
import io.github.posaydone.filmix.tv.ui.common.Loading
import io.github.posaydone.filmix.tv.ui.common.gradientOverlay
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding
import kotlinx.coroutines.launch

private const val TAG = "ShowDetailsScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailsScreen(
    modifier: Modifier = Modifier,
    showId: Int,
    navigateToMoviePlayer: (showId: Int) -> Unit,
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
                fullShow = s.fullShow,
                showProgress = s.showProgress,
                showImages = s.showImages,
                showTrailers = s.showTrailers,
                toggleFavorites = s.toggleFavorites,
                goToMoviePlayer = {
                    navigateToMoviePlayer(showId)
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
    fullShow: FullShow,
    showProgress: ShowProgress?,
    showImages: ShowImages,
    showTrailers: ShowTrailers?,
    toggleFavorites: () -> Unit,
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
                imageUrl = fullShow.backdropUrl ?: showImages.frames.firstOrNull()?.url
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
                    logoUrl = fullShow.logoUrl,
                    title = fullShow.title,
                    description = fullShow.description ?: fullShow.shortDescription ?: showDetails.shortStory,
                    rating = Rating(
                        kp = fullShow.ratingKp,
                        imdb = fullShow.ratingImdb,
                        filmCritics = .0,
                        russianFilmCritics = .0,
                        await = .0
                    ),
                    votes = Votes(
                        kp = fullShow.votesKp,
                        imdb = fullShow.votesImdb,
                        filmCritics = 0,
                        russianFilmCritics = 0,
                        await = 0
                    ),
                    genres = fullShow.genres.map { g ->
                        KinopoiskGenre(
                            g
                        )
                    },
                    countries = fullShow.countries.map { c -> KinopoiskCountry(c) },
                    year = fullShow.year,
                    movieLength = fullShow.movieLength ?: showDetails.duration,
                    seriesLength = fullShow.seriesLength,
                    ageRating = fullShow.ageRating.toString()
                )
                ShowDetailsButtons(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    goToMoviePlayer = goToMoviePlayer,
                    toggleFavorites = toggleFavorites,
                    isFavorite = showDetails.isFavorite == true
                )
            }
        }

    }
}

//@Preview(device = "id:tv_4k")
//@Composable
//private fun ShowDetailsScreenPreview() {
//
//    Details(
//        showDetails = ShowDetails(
//            id = 1,
//            category = "TV Show",
//            title = "Mock Show",
//            originalTitle = "Mock Show Original",
//            year = 2023,
//            updated = "2024-01-18",
//            actors = arrayListOf(
//                KinopoiskPerson(id = "1", name = "John Doe", poster = "https://example.com/john_doe.jpg"),
//                KinopoiskPerson(id = "2", name = "Jane Doe", poster = "https://example.com/jane_doe.jpg")
//            ),
//            directors = arrayListOf(
//                KinopoiskPerson(
//                    id = "3", name = "Director One", poster = "https://example.com/director_one.jpg"
//                ), KinopoiskPerson(
//                    id = "4", name = "Director Two", poster = "https://example.com/director_two.jpg"
//                )
//            ),
//            lastEpisode = LastEpisode(
//                season = 2, episode = "10", translation = "Dub", date = "2024-01-15"
//            ),
//            maxEpisode = MaxEpisode(season = 2, episode = 10),
//            countries = arrayListOf(
//                Country(id = 1, name = "USA"), Country(id = 2, name = "UK")
//            ),
//            genres = arrayListOf(
//                Genre(id = 1, name = "Drama", alt_name = "drama"),
//                Genre(id = 2, name = "Sci-Fi", alt_name = "sci-fi")
//            ),
//            poster = "https://example.com/mock_show_poster.jpg",
//            rip = "WEBRip",
//            quality = "1080p",
//            votesPos = 5000,
//            votesNeg = 200,
//            ratingImdb = 8.5,
//            ratingKinopoisk = 8.2,
//            url = "https://example.com/mock_show",
//            duration = 120,
//            votesIMDB = 10000,
//            votesKinopoisk = 8000,
//            idKinopoisk = 123456,
//            mpaa = "PG-13",
//            slogan = "A mock show for testing",
//            shortStory = "This is a mock show created for testing purposes.",
//            status = null,
//            isFavorite = true,
//            isDeferred = false,
//            isHdr = true
//        ),
//        fullShow = FullShow(
//            id = 1,
//            title = "Mock Show",
//            originalTitle = "Mock Show Original",
//            year = 2023,
//            posterUrl = "https://example.com/mock_show_poster.jpg",
//            backdropUrl = "https://example.com/mock_show_backdrop.jpg",
//            logoUrl = null,
//            description = "This is a mock show created for testing purposes.",
//            shortDescription = "Test description",
//            ratingKp = 8.2,
//            ratingImdb = 8.5,
//            votesKp = 8000,
//            votesImdb = 10000,
//            isSeries = false,
//            isShow = false,
//            genres = listOf("Drama", "Sci-Fi"),
//            countries = listOf("USA", "UK"),
//            ageRating = 13,
//            movieLength = 120,
//            seriesLength = null,
//            quality = "1080p",
//            status = "Completed",
//            votesPos = 5000,
//            votesNeg = 200,
//            tmdbPosterPaths = emptyList(),
//            tmdbBackdropPaths = emptyList(),
//            tmdbLogoPaths = emptyList()
//        ),
//        showProgress = null,
//        showImages = ShowImages(
//            frames = listOf(
//                ShowImage(size = 1920, title = "Frame 1", url = "https://example.com/frame1.jpg"),
//                ShowImage(size = 1920, title = "Frame 2", url = "https://example.com/frame2.jpg")
//            ), posters = listOf(
//                ShowImage(size = 1080, title = "Poster 1", url = "https://example.com/poster1.jpg"),
//                ShowImage(size = 1080, title = "Poster 2", url = "https://example.com/poster2.jpg")
//            )
//        ),
//        showTrailers = null,
//        toggleFavorites = {},
//        goToMoviePlayer = {},
//    )
//}

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
private fun ShowDetailsButtons(
    modifier: Modifier = Modifier,
    goToMoviePlayer: () -> Unit,
    toggleFavorites: () -> Unit,
    isFavorite: Boolean,
) {
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LargeButton(
            onClick = goToMoviePlayer,
            style = LargeButtonStyle.FILLED,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = stringResource(R.string.playString),
                style = MaterialTheme.typography.titleMedium
            )
        }

        LargeButton(
            onClick = toggleFavorites,
            style = if (isFavorite) LargeButtonStyle.FILLED else LargeButtonStyle.OUTLINED
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = if (isFavorite) Icons.Rounded.BookmarkRemove else Icons.Rounded.BookmarkAdd,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
