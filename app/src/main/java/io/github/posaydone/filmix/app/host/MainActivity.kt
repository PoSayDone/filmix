package io.github.posaydone.filmix.app.host

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.posaydone.filmix.presentation.navigation.MobileNavGraph
import io.github.posaydone.filmix.presentation.navigation.TvNavGraph
import io.github.posaydone.filmix.presentation.theme.MainTheme


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
                MainTheme(darkTheme = true)
                {
                    TvNavGraph()
                }
            } else {
                MainTheme {
                    MobileNavGraph()
                }
            }
        }
    }
}