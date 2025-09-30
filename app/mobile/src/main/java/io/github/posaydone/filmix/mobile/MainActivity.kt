package io.github.posaydone.filmix.mobile

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.posaydone.filmix.core.model.AuthEvent
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.mobile.navigation.NavGraph
import io.github.posaydone.filmix.mobile.ui.theme.FilmixTheme
import io.github.posaydone.filmix.mobile.utils.AppUpdateManager
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
        installSplashScreen()
        
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
        
        enableEdgeToEdge()
        setContent {
            FilmixTheme {
                NavGraph(
                    sessionManager = sessionManager,
                    authEventFlow = authEventFlow
                )
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Check if the app should enter PiP mode
        if (isPipModeAvailable()) {
            enterPipMode()
        }
    }

    private fun isPipModeAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                packageManager.hasSystemFeature("android.software.picture_in_picture") &&
                !isInPictureInPictureMode
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()

            enterPictureInPictureMode(params)
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        
        if (isInPictureInPictureMode) {
            // In PiP mode - lock to landscape orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            // Not in PiP mode - restore the desired orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }
    
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        // This method is called when the activity becomes or stops being the top resumed activity
        if (!isTopResumedActivity && isInPictureInPictureMode) {
            // The app is going into background but is in PiP mode - keep it running
            Log.d("MainActivity", "App going to background in PiP mode")
        }
    }
}