package io.github.posaydone.filmix.tv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.posaydone.filmix.core.model.AuthEvent
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.tv.navigation.NavGraph
import io.github.posaydone.filmix.tv.ui.theme.FilmixTheme
import io.github.posaydone.filmix.tv.utils.AppUpdateManager
import io.github.posaydone.filmix.core.network.service.GithubApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager // Inject SessionManager

    @Inject
    @JvmSuppressWildcards
    lateinit var authEventFlow: SharedFlow<AuthEvent> // Inject the flow
    
    @Inject
    lateinit var githubApiService: GithubApiService // Inject GitHub API service

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the update manager
        appUpdateManager = AppUpdateManager(this, githubApiService)
        
        // Check for updates on app start (with a delay to not block UI)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Thread.sleep(2000) // Small delay to not impact startup time
                val updateAvailable = appUpdateManager.autoCheckForUpdate()
                if (updateAvailable) {
                    Log.d("MainActivity", "Update available - notification can be shown to user")
                    // Optionally show notification to user about available update
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error checking for updates", e)
            }
        }
        
        setContent {
            FilmixTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        NavGraph(
                            sessionManager = sessionManager,
                            authEventFlow = authEventFlow
                        )
                    }
                }
            }
        }
    }
}