package io.github.posaydone.filmix.presentation.ui.authScreen

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.presentation.navigation.MobileScreens


private var access: String? = null
private var refresh: String? = null

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel(),
) {
    val sessionManager = viewModel.sessionManager

    val mUrl = "https://filmix.tv/auth/login"
    val savedToken = sessionManager.fetchAccessToken()
    val savedRefresh = sessionManager.fetchRefreshToken()

    if (savedToken != null && savedRefresh != null) {
        navController.navigate(MobileScreens.Main) {
            popUpTo<MobileScreens.Auth> {
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
            access = it.replace("\"", "")
        }
        view.evaluateJavascript("(function() { return localStorage.getItem(\"refresh\"); })();") {
            refresh = it.replace("\"", "")
        }
        if (access == "null" || access == "")
            access = null
        if (refresh == "null" || refresh == "")
            refresh = null
    }
    checkAndStoreTokens(sessionManager)
}


private fun checkAndStoreTokens(sessionManager: SessionManager) {
    if (access != null && refresh != null) {
        sessionManager.saveAccessToken(
            access!!,
            expiresIn = System.currentTimeMillis() + (50 * 60 * 1000)
        )
        sessionManager.saveRefreshToken(refresh!!)
    }
}