package io.github.posaydone.filmix.mobile

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.mobile.navigation.NavGraph
import io.github.posaydone.filmix.mobile.ui.theme.FilmixTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager // Inject SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            FilmixTheme {
                NavGraph(sessionManager = sessionManager)
            }
        }
    }

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//        enterPipMode()
//    }
//
//    private fun enterPipMode() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val params = PictureInPictureParams.Builder()
//                .setAspectRatio(Rational(16, 9))
//                .build()
//
//            enterPictureInPictureMode(params)
//        }
//    }
}