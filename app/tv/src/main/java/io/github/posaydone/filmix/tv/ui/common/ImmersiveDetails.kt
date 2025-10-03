package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.common.utils.formatDuration
import io.github.posaydone.filmix.core.common.utils.formatVoteCount
import io.github.posaydone.filmix.core.model.KinopoiskCountry
import io.github.posaydone.filmix.core.model.KinopoiskGenre
import io.github.posaydone.filmix.core.model.Rating
import io.github.posaydone.filmix.core.model.Votes
import kotlin.math.max

/**
 * A reusable composable for displaying a full-screen background image with a crossfade animation.
 */
@Composable
fun ImmersiveBackground(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = imageUrl,
        label = "BackgroundCrossfade",
        animationSpec = tween(durationMillis = 500)
    ) { image ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(image).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )
    }
}

/**
 * A reusable composable that displays the main details of a movie or show,
 * including a logo or title, metadata, and description.
 */
@Composable
fun ImmersiveDetails(
    logoUrl: String?,
    title: String,
    description: String?,
    rating: Rating?,
    votes: Votes?,
    genres: List<KinopoiskGenre>?,
    countries: List<KinopoiskCountry>?,
    year: Int?,
    seriesLength: Int?,
    movieLength: Int?,
    ageRating: String,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo or Title
        if (!logoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(logoUrl).build(),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.sizeIn(
                    maxWidth = screenWidth * 0.4f, maxHeight = screenHeight * 0.16f
                )
            )
        } else {
            Text(
                text = title, style = MaterialTheme.typography.displaySmall
            )
        }

        MetadataRow(
            rating = rating,
            votes = votes,
            year = year,
            genres = genres,
            totalMinutes = max(movieLength ?: 0, seriesLength ?: 0),
            countries = countries,
            ageRating = ageRating
        )

        if (!description.isNullOrBlank()) {
            Text(
                modifier = Modifier.sizeIn(
                    maxWidth = 400.dp
                ), text = description, style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 0.sp, lineHeight = 22.sp
                ), maxLines = 3, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetadataRow(
    rating: Rating?,
    votes: Votes?,
    year: Int?,
    genres: List<KinopoiskGenre>?,
    totalMinutes: Int,
    countries: List<KinopoiskCountry>?,
    ageRating: String,
) {
    val context = LocalContext.current
    val metadataParts = remember(rating, votes, year, genres, totalMinutes, countries, ageRating) {
        buildList {
            val ratingText = rating?.kp?.let { "%.1f".format(it) }
            val votesText = votes?.kp?.let { formatVoteCount(it) }
            if (ratingText != null) {
                add(if (votesText != null) "$ratingText ($votesText)" else ratingText)
            }
            year?.let { add(it.toString()) }
            genres?.mapNotNull { it.name }?.take(2)?.joinToString(", ")
                ?.let { if (it.isNotEmpty()) add(it) }
            val durationText = formatDuration(context, totalMinutes)
            if (durationText.isNotEmpty()) add(durationText)
            countries?.mapNotNull { it.name }?.take(2)?.joinToString(", ")
                ?.let { if (it.isNotEmpty()) add(it) }
            ageRating?.let { add("$it+") }
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        metadataParts.forEachIndexed { index, part ->
            Text(
                text = part,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            if (index < metadataParts.lastIndex) {
                Text(
                    text = "•",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun Modifier.gradientOverlay(gradientColor: Color): Modifier = drawWithCache {
    val horizontalGradient = Brush.horizontalGradient(
        colors = listOf(
            gradientColor, Color.Transparent
        ), startX = size.width.times(0.2f), endX = size.width.times(0.5f)
    )
    val verticalGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent, gradientColor
        ), endY = size.width.times(0.5f)
    )
    val linearGradient = Brush.linearGradient(
        colors = listOf(
            gradientColor, Color.Transparent
        ), start = Offset(
            size.width.times(0.2f), size.height.times(0.3f)
        ), end = Offset(
            size.width.times(0.9f), 0f
        )
    )

    onDrawWithContent {
        drawContent()
        drawRect(horizontalGradient)
        drawRect(verticalGradient)
        drawRect(linearGradient)
    }
}
