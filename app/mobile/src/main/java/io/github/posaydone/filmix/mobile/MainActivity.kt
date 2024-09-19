package io.github.posaydone.filmix.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.posaydone.filmix.mobile.navigation.NavGraph
import io.github.posaydone.filmix.mobile.ui.theme.FilmixTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmixTheme {
                NavGraph()
            }
        }
    }
}