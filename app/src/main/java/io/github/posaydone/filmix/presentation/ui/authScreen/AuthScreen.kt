package io.github.posaydone.filmix.presentation.ui.authScreen

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.data.pref.SessionManager
import io.github.posaydone.filmix.presentation.navigation.Screens


private var token: String? = null
private var refresh: String? = null

@Composable
fun AuthScreen(navController: NavHostController) {
    val mUrl = "https://filmix.tv/auth/login"
    val sessionManager = SessionManager(LocalContext.current)
    val savedToken = sessionManager.fetchAccessToken()
    val savedRefresh = sessionManager.fetchRefreshToken()

    if (savedToken != null && savedRefresh != null) {
        navController.navigate(Screens.Main) {
            popUpTo<Screens.Auth> {
                inclusive = true
            }
        }
    }

    AndroidView(factory = {
        WebView(it).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            this.settings.javaScriptEnabled = true
            this.settings.domStorageEnabled = true
            this.webViewClient = CustomWebViewClient(sessionManager)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}

class CustomWebViewClient(private val sessionManager: SessionManager) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        handleToken(url.toString(), view, sessionManager)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        handleToken(url.toString(), view, sessionManager)
    }
}

private fun handleToken(url: String, view: WebView?, sessionManager: SessionManager) {
    if (view != null) {
        view.evaluateJavascript("(function() { return localStorage.getItem(\"token\"); })();") {
            token = it.replace("\"", "")
        }
        view.evaluateJavascript("(function() { return localStorage.getItem(\"refresh\"); })();") {
            refresh = it.replace("\"", "")
        }
        if (token == "null" || token == "")
            token = null
        if (refresh == "null" || refresh == "")
            refresh = null
    }
    checkAndStoreTokens(sessionManager)
}


private fun checkAndStoreTokens(sessionManager: SessionManager) {
    if (token != null && refresh != null) {
        sessionManager.saveAccessToken(
            token!!,
            expiresIn = System.currentTimeMillis() + (50 * 60 * 1000)
        )
        sessionManager.saveRefreshToken(refresh!!)
    }
}