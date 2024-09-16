package io.github.posaydone.filmix.app.host

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.posaydone.filmix.presentation.navigation.MobileNavGraph
import io.github.posaydone.filmix.presentation.navigation.TvNavGraph
import io.github.posaydone.filmix.presentation.theme.MobileTheme
import io.github.posaydone.filmix.presentation.theme.TvTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )
        super.onCreate(savedInstanceState)

        val isTv = this.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)

        actionBar?.hide()
        setContent {
            if (isTv) {
                TvTheme(darkTheme = true)
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurface
                        ) {
                            TvNavGraph(
                            )
                        }
                    }
                }
            } else {
                MobileTheme {
                    MobileNavGraph()
                }
            }
        }
    }
}