package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f),
    Horizontal(16f / 9f);
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowsRow(
    showList: ShowList,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onMovieSelected: (show: Show) -> Unit = {},
) {
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1.0f)
                    .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
            )
        }
        AnimatedContent(
            targetState = showList,
            label = "",
        ) { movieState ->
            LazyRow(
                contentPadding = PaddingValues(
                    start = startPadding,
                    end = endPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer()
            ) {
                itemsIndexed(movieState, key = { _, show -> show.id }) { index, show ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    MoviesRowItem(
                        modifier = itemModifier.weight(1f),
                        index = index,
                        itemDirection = itemDirection,
                        onMovieSelected = {
                            lazyRow.saveFocusedChild()
                            onMovieSelected(it)
                        },
                        show = show,
                        showItemTitle = showItemTitle,
                        showIndexOverImage = showIndexOverImage
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MoviesRowItem(
    index: Int,
    show: Show,
    onMovieSelected: (Show) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    onMovieFocused: (Show) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }

    ShowCard(
        onClick = { onMovieSelected(show) },
        title = {
            MoviesRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                show = show
            )
        },
        modifier = Modifier
            .width(148.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onMovieFocused(show)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
    ) {
        MoviesRowItemImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            show = show,
            index = index
        )
    }
}

@Composable
private fun MoviesRowItemImage(
    show: Show,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImage(
            show = show,
            modifier = modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (showIndexOverImage) {
                        drawRect(
                            color = Color.Black.copy(
                                alpha = 0.1f
                            )
                        )
                    }
                },
        )
        if (showIndexOverImage) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "#${index.inc()}",
                style = MaterialTheme.typography.displayLarge
                    .copy(
                        shadow = Shadow(
                            offset = Offset(0.5f, 0.5f),
                            blurRadius = 5f
                        ),
                        color = Color.White
                    ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MoviesRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    show: Show,
    modifier: Modifier = Modifier,
) {
    if (showItemTitle) {
        val movieNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            label = "",
        )
        Text(
            text = show.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(movieNameAlpha)
                .padding(top = 16.dp),
            softWrap = true,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun ImmersiveListMoviesRow(
//    movieList: MovieList,
//    modifier: Modifier = Modifier,
//    itemDirection: ItemDirection = ItemDirection.Vertical,
//    startPadding: Dp = rememberChildPadding().start,
//    endPadding: Dp = rememberChildPadding().end,
//    title: String? = null,
//    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
//        fontWeight = FontWeight.Medium,
//        fontSize = 30.sp
//    ),
//    showItemTitle: Boolean = true,
//    showIndexOverImage: Boolean = false,
//    onMovieSelected: (Movie) -> Unit = {},
//    onMovieFocused: (Movie) -> Unit = {},
//) {
//    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }
//
//    Column(
//        modifier = modifier.focusGroup()
//    ) {
//        if (title != null) {
//            Text(
//                text = title,
//                style = titleStyle,
//                modifier = Modifier
//                    .alpha(1f)
//                    .padding(start = startPadding)
//                    .padding(vertical = 16.dp)
//            )
//        }
//        AnimatedContent(
//            targetState = movieList,
//            label = "",
//        ) { movieState ->
//            LazyRow(
//                contentPadding = PaddingValues(start = startPadding, end = endPadding),
//                horizontalArrangement = Arrangement.spacedBy(20.dp),
//                modifier = Modifier
//                    .focusRequester(lazyRow)
//                    .focusRestorer {
//                        firstItem
//                    }
//            ) {
//                itemsIndexed(
//                    movieState,
//                    key = { _, movie ->
//                        movie.id
//                    }
//                ) { index, movie ->
//                    val itemModifier = if (index == 0) {
//                        Modifier.focusRequester(firstItem)
//                    } else {
//                        Modifier
//                    }
//                    MoviesRowItem(
//                        modifier = itemModifier.weight(1f),
//                        index = index,
//                        itemDirection = itemDirection,
//                        onMovieSelected = {
//                            lazyRow.saveFocusedChild()
//                            onMovieSelected(it)
//                        },
//                        onMovieFocused = onMovieFocused,
//                        movie = movie,
//                        showItemTitle = showItemTitle,
//                        showIndexOverImage = showIndexOverImage
//                    )
//                }
//            }
//        }
//    }
//}

