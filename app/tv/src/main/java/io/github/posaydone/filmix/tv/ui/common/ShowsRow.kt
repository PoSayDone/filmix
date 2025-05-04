package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.tv.R
import io.github.posaydone.filmix.tv.ui.screen.homeScreen.rememberChildPadding
import io.github.posaydone.filmix.tv.ui.utils.bringIntoViewIfChildrenAreFocused

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f), Horizontal(16f / 9f);
}

private const val TAG = "ShowsRow"

@Composable
fun ShowsRow(
    cardWidth: Dp = 148.dp,
    showList: ShowList,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium, fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onShowSelected: (show: Show) -> Unit = {},
    onShowFocused: ((Show) -> Unit)? = {},
    requestInitialFocus: Boolean = false,
) {
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }
    var isInitialyFocused = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.focusGroup()
            .onGloballyPositioned {
                if (!isInitialyFocused.value && requestInitialFocus) {
                    lazyRow.requestFocus()
                    isInitialyFocused.value = true
                }
            }
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
                    .focusRestorer(firstItem)
            ) {
                itemsIndexed(movieState, key = { _, show -> show.id }) { index, show ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    ShowsRowItem(
                        modifier = itemModifier.weight(1f),
                        index = index,
                        cardWidth = cardWidth,
                        itemDirection = itemDirection,
                        onShowSelected = {
                            lazyRow.saveFocusedChild()
                            onShowSelected(it)
                        },
                        onShowFocused = { onShowFocused?.invoke(show) },
                        show = show,
                        showItemTitle = showItemTitle,
                        showIndexOverImage = showIndexOverImage
                    )
                }
            }
        }
    }
}

@Composable
fun ImmersiveShowsRow(
    showList: ShowList,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium, fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onShowSelected: (Show) -> Unit = {},
    requestInitialFocus: Boolean = false,
) {

    var isListFocused by remember { mutableStateOf(false) }
    var selectedShow by remember(showList) { mutableStateOf(showList.first()) }
    val animatedCardWidth by animateDpAsState(
        targetValue = if (isListFocused) 110.dp else 148.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cardWidthAnimation"
    )

    Box(
        modifier
            .fillMaxSize()
            .bringIntoViewIfChildrenAreFocused()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.run { screenHeightDp.dp } - 32.dp)) {
            AnimatedVisibility(
                visible = isListFocused, enter = fadeIn(), exit = fadeOut()
            ) {
                Background(
                    show = selectedShow
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .gradientOverlay(MaterialTheme.colorScheme.surface)
                )
                AnimatedContent(
                    targetState = selectedShow,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith fadeOut(
                            animationSpec = tween(durationMillis = 500)
                        )
                    },
                    label = "",
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterStart)
                        .padding(start = startPadding, end = 30.dp, top = 30.dp)
                ) { show ->
                    MovieDescription(show)
                }
            }
        }

        Box(
            Modifier
                .align(Alignment.BottomStart)
                .onFocusChanged { isListFocused = it.hasFocus }) {
            ShowsRow(
                requestInitialFocus = requestInitialFocus,
                cardWidth = animatedCardWidth,
                showList = showList,
                itemDirection = itemDirection,
                startPadding = startPadding,
                endPadding = endPadding,
                title = title,
                titleStyle = titleStyle,
                showItemTitle = showItemTitle,
                showIndexOverImage = showIndexOverImage,
                onShowSelected = onShowSelected,
                onShowFocused = {
                    selectedShow = it
                },
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ShowsRowItem(
    index: Int,
    show: Show,
    onShowSelected: (Show) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    onShowFocused: (Show) -> Unit = {},
    cardWidth: Dp = 148.dp,
) {
    var isFocused by remember { mutableStateOf(false) }

    ShowCard(
        onClick = { onShowSelected(show) }, title = {
            ShowsRowItemText(
                showItemTitle = showItemTitle, isItemFocused = isFocused, show = show
            )
        }, modifier = Modifier
            .width(cardWidth)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onShowFocused(show)
                }
            }
            .then(modifier)) {
        ShowsRowItemImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            show = show,
            index = index
        )
    }
}

@Composable
private fun ShowsRowItemImage(
    show: Show,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier, contentAlignment = Alignment.CenterStart) {
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
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = Shadow(
                        offset = Offset(0.5f, 0.5f), blurRadius = 5f
                    ), color = Color.White
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShowsRowItemText(
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

@Composable
private fun Background(
    show: Show,
) {
    val image = show.poster

    Crossfade(
        targetState = image, label = "PosterCrossfade", animationSpec = tween(durationMillis = 500)
    ) { showingImage ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(showingImage).build(),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MovieDescription(
    show: Show,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = show.title, style = MaterialTheme.typography.displaySmall)
        Text(
            modifier = Modifier.fillMaxWidth(0.5f),
            text = show.original_name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontWeight = FontWeight.Light
        )
    }
}

private fun Modifier.gradientOverlay(gradientColor: Color): Modifier = drawWithCache {
    val horizontalGradient = Brush.horizontalGradient(
        colors = listOf(
            gradientColor, Color.Transparent
        ), startX = size.width.times(0.2f), endX = size.width.times(0.5f)
    )
    val verticalGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent, gradientColor
        ), endY = size.width.times(0.4f)
    )
    val linearGradient = Brush.linearGradient(
        colors = listOf(
            gradientColor, Color.Transparent
        ), start = Offset(
            size.width.times(0.2f), size.height.times(0.5f)
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