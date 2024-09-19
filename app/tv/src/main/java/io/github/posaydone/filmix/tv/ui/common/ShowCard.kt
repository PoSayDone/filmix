package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import io.github.posaydone.filmix.tv.ui.theme.FilmixBorderWidth

@Composable
fun ShowCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    StandardCardContainer(
        modifier = modifier,
        title = title,
        imageCard = {
            Surface(
                onClick = onClick,
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = FilmixBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
                content = image
            )
        },
    )
}
