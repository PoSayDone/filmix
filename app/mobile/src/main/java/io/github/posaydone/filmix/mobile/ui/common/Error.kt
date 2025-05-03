package io.github.posaydone.filmix.mobile.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Error(
    modifier: Modifier = Modifier, onRetry: () -> Unit,
    children: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(24.dp),
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
            onClick = onRetry,
        ) {
            Text("Retry")
        }
        children?.invoke()
    }
}
