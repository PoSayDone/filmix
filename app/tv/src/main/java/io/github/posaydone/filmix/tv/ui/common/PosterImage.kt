package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.model.Show

@Composable
fun PosterImage(
    contentDescritpion: String?,
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(imageUrl)
            .build(),
        contentDescription = contentDescritpion,
        contentScale = ContentScale.Crop
//        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(show.name),
    )
}