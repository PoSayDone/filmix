package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.focusable(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = 0.75f,
            modifier = Modifier.size(64.dp),
        )
    }
}