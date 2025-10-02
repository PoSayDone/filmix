@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.posaydone.filmix.mobile.ui.screen.showDetailsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ShowDetailsScreenViewModel
import io.github.posaydone.filmix.core.common.utils.formatDuration
import io.github.posaydone.filmix.core.common.utils.formatVoteCount
import io.github.posaydone.filmix.core.model.KinopoiskMovie
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.mobile.ui.common.Error
import io.github.posaydone.filmix.mobile.ui.common.LargeButton
import io.github.posaydone.filmix.mobile.ui.common.LargeButtonStyle
import io.github.posaydone.filmix.mobile.ui.common.Loading
import kotlin.math.max

val TAG = "ShowDetailsScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailsScreen(
    showId: Int,
    navigateToMoviePlayer: (showId: Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
            NavigationBarDefaults.windowInsets.union(WindowInsets.statusBars)
        )
    ) { paddingValues ->
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
                    toggleFavorites = s.toggleFavorites,
                    navigateToMoviePlayer = { navigateToMoviePlayer(showId) },
                    navigateBack = navigateBack,
                    modifier = modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun Details(
    showDetails: ShowDetails,
    showProgress: ShowProgress,
    showImages: ShowImages,
    showTrailers: ShowTrailers,
    kinopoiskMovie: KinopoiskMovie?,
    toggleFavorites: () -> Unit,
    navigateToMoviePlayer: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = modifier) {
        val imageUrl = kinopoiskMovie?.backdrop?.url ?: showImages.frames.firstOrNull()?.url
        ?: showDetails.poster

        val headerHeight = 400.dp
        val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                .build(),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .graphicsLayer {
                    val scrollOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
                    alpha = (1f - (scrollOffset / headerHeightPx)).coerceIn(0f, 1f)
                })

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Spacer(modifier = Modifier.height(300.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent, MaterialTheme.colorScheme.background
                                ), startY = 0f, endY = with(LocalDensity.current) { 50.dp.toPx() })
                        )
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TitleSection(
                            title = showDetails.title, logoUrl = kinopoiskMovie?.logo?.url
                        )
                        MetadataColumn(
                            ratingKp = kinopoiskMovie?.rating?.kp
                            ?: showDetails.ratingKinopoisk,
                            votesKp = kinopoiskMovie?.votes?.kp ?: showDetails.votesKinopoisk,
                            originalTitle = showDetails.originalTitle,
                            title = showDetails.title,
                            year = kinopoiskMovie?.year ?: showDetails.year,
                            genres = (kinopoiskMovie?.genres?.map { it.name }
                                ?: showDetails.genres.map { it.name }),
                            countries = (kinopoiskMovie?.countries?.map { it.name }
                                ?: showDetails.countries.map { it.name }),
                            totalMinutes = max(
                                kinopoiskMovie?.movieLength ?: 0, kinopoiskMovie?.seriesLength ?: 0
                            ).takeIf { it > 0 } ?: showDetails.duration ?: 0,
                            ageRating = kinopoiskMovie?.ageRating
                                ?: showDetails.mpaa?.filter { it.isDigit() }?.toIntOrNull()
                        )

                        ActionButtons(
                            goToMoviePlayer = navigateToMoviePlayer,
                            toggleFavorites = toggleFavorites,
                            isFavorite = showDetails.isFavorite == true
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(vertical = 24.dp),
                    ) {
                        DescriptionSection(
                            description = kinopoiskMovie?.description
                                ?: kinopoiskMovie?.shortDescription ?: showDetails.shortStory
                        )
                    }
                }
            }
        }


        TransparentTopAppBar(navigateBack = navigateBack)
    }
}


@Composable
private fun TitleSection(title: String, logoUrl: String?) {
    if (logoUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(logoUrl).build(),
            contentDescription = title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Take up to 80% of width
                .height(80.dp)
        )
    } else {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun MetadataColumn(
    ratingKp: Double?,
    votesKp: Int?,
    originalTitle: String,
    title: String,
    year: Int?,
    genres: List<String>,
    countries: List<String>,
    totalMinutes: Int,
    ageRating: Int?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "%.1f".format(
                    ratingKp ?: 0.0
                ) + " (${formatVoteCount(votesKp ?: 0)})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (originalTitle.isNotBlank() && originalTitle != title) {
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = originalTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = year?.toString() ?: "",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = genres.take(2).joinToString(", "),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = countries.take(2).joinToString(", "),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = formatDuration(context, totalMinutes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ageRating?.let {
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "$it+",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    goToMoviePlayer: () -> Unit,
    toggleFavorites: () -> Unit,
    isFavorite: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LargeButton(
            onClick = goToMoviePlayer
        ) {
            Icon(
                contentDescription = "Play",
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Rounded.PlayArrow,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.playString))
        }
        LargeButton(
            style = LargeButtonStyle.OUTLINED,
            onClick = toggleFavorites,
            colors = if (isFavorite == true) ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) else ButtonDefaults.outlinedButtonColors()
        ) {
            Icon(
                contentDescription = "Favorite",
                modifier = Modifier.size(28.dp),
                imageVector = if (isFavorite) Icons.Rounded.BookmarkRemove else Icons.Rounded.BookmarkAdd,
            )
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun TransparentTopAppBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            FilledIconButton(onClick = navigateBack, modifier = Modifier.size(42.dp)) {
                Icon(
                    contentDescription = "Back", imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )
            }
        })
}
