package io.github.posaydone.filmix.presentation.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Typography
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme

@Composable
fun TvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {

        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme( // …3
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
