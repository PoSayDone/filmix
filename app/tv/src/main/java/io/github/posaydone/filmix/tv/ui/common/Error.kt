package io.github.posaydone.filmix.tv.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text

@Composable
fun Error(modifier: Modifier = Modifier) {
    Text(
//        text = stringResource(id = R.string.message_error),
        text = "Error",
        modifier = modifier
    )
}
