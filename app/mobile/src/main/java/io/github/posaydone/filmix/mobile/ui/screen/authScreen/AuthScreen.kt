package io.github.posaydone.filmix.mobile.ui.screen.authScreen

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.sharedViewModel.AuthScreenViewModel
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.mobile.navigation.Screens

private var access: String? = null
private var refresh: String? = null

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel(),
) {
    val sessionManager = viewModel.sessionManager
    val navigateToHome = {
        navController.navigate(Screens.Main) {
            popUpTo<Screens.Auth> {
                inclusive = true
            }
        }
    }

    val mUrl = "https://filmix.tv/auth/login"
    val savedToken = sessionManager.fetchAccessToken()
    val savedRefresh = sessionManager.fetchRefreshToken()

    if (savedToken != null && savedRefresh != null) {
        navigateToHome()
    }

    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            this.settings.javaScriptEnabled = true
            this.settings.domStorageEnabled = true
            this.webViewClient = CustomWebViewClient(sessionManager, navigateToHome)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}

class CustomWebViewClient(
    private val sessionManager: SessionManager,
    private val navigateToHome: () -> Unit,
) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        handleToken(url.toString(), view, sessionManager, navigateToHome)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        handleToken(url.toString(), view, sessionManager, navigateToHome)
    }
}

private fun handleToken(
    url: String,
    view: WebView?,
    sessionManager: SessionManager,
    navigateToHome: () -> Unit,
) {
    if (view != null) {
        view.evaluateJavascript(
            "(function()" + "{ return localStorage.getItem(\"token\"); })();"
        ) {
            access = it.replace("\"", "")
        }
        view.evaluateJavascript(
            "(function()" + " { return localStorage.getItem(\"refresh\"); })();"
        ) {
            refresh = it.replace("\"", "")
        }
        if (access == "null" || access == "") access = null
        if (refresh == "null" || refresh == "") refresh = null
    }
    checkAndStoreTokens(sessionManager, navigateToHome)
}


private fun checkAndStoreTokens(sessionManager: SessionManager, navigateToHome: () -> Unit) {
    if (access != null && refresh != null) {
        sessionManager.saveAccessToken(
            access!!, expiresIn = System.currentTimeMillis() + (50 * 60 * 1000)
        )
        sessionManager.saveRefreshToken(refresh!!)
        navigateToHome()
    }
}