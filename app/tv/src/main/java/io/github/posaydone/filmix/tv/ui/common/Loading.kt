package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Loading")
//            CircularProgressIndicator(
//                modifier = Modifier.width(64.dp),
//                color = MaterialTheme.colorScheme.secondary,
//                trackColor = MaterialTheme.colorScheme.surfaceVariant,
//            )
    }
}