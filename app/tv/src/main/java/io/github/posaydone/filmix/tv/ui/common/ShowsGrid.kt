package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.tv.ui.theme.FilmixBottomListPadding

@Composable
fun ShowsGrid(
//    state: LazyGridState,
    modifier: Modifier = Modifier,
    showList: ShowList,
    onShowClick: (showid: Int) -> Unit,
) {
    LazyVerticalGrid(
//        state = state,
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(6),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            bottom = FilmixBottomListPadding,
            top = FilmixBottomListPadding
        ),
    ) {
        items(showList, key = { it.id }) { show ->
            ShowCard(
                onClick = { onShowClick(show.id) },
                modifier = Modifier.aspectRatio(2f / 3f),
            ) {
                PosterImage(show = show, modifier = Modifier.fillMaxSize())
            }
        }
    }
}