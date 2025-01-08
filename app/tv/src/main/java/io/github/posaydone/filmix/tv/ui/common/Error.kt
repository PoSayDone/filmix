package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun Error(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.padding(48.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp, alignment = Alignment.CenterVertically
        ),
    ) {
        Text(
            text = "Error", style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Filmix service is unavailable or you don't have internet connection. Try again later.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester)
                .focusable(),
            onClick = onRetry,
        ) {
            Text("Retry")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
