package io.github.posaydone.filmix.mobile.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.mobile.navigation.Screens

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowsRow(
    showList: ShowList,
    modifier: Modifier = Modifier,
    title: String,
    navController: NavController,
) {

    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )

        AnimatedContent(
            targetState = showList,
            label = "",
        ) { showList ->
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer {
                        firstItem
                    }
            ) {

                itemsIndexed(showList, key = { _, show -> show.id }) { index, show ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }
                    ShowsRowItem(
                        modifier = itemModifier.weight(1f),
                        index = index,
                        onMovieSelected = {
                            lazyRow.saveFocusedChild()
                            navController.navigate(Screens.Main.Details(show.id)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onMovieFocused = {},
                        show = show,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ShowsRowItem(
    index: Int,
    show: Show,
    onMovieSelected: (Show) -> Unit,
    modifier: Modifier = Modifier,
    onMovieFocused: (Show) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }

    ShowCard(
        show = show,
        onClick = { onMovieSelected(show) },
        modifier = Modifier
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
    )
}