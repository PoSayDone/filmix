package io.github.posaydone.filmix.presentation.common.tv

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.data.entities.Show

@Composable
fun PosterImage(
    show: Show,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(show.poster)
            .build(),
        contentDescription = show.title,
        contentScale = ContentScale.Crop
//        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(show.name),
    )
}