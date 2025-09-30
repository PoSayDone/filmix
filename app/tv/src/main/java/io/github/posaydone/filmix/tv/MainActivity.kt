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
import io.github.posaydone.filmix.core.data.repository.GithubRepository
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

    private val updateManagerViewModel: UpdateManagerViewModel by viewModels()
    private lateinit var appUpdateService: AppUpdateService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the update service
        appUpdateService = AppUpdateService(this, updateManagerViewModel)
        
        // Check for updates on app start (with lifecycle-aware scope)
        lifecycleScope.launch {
            try {
                // Small delay to not impact startup time
                kotlinx.coroutines.delay(2000)
                appUpdateService.checkForUpdate()
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