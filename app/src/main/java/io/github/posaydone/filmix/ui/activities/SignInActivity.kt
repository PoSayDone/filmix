package io.github.posaydone.filmix.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import io.github.posaydone.filmix.data.api.SessionManager
import io.github.posaydone.filmix.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private val TAG: String = "signin"
    private lateinit var webView: WebView
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sessionManager: SessionManager

    private var token: String? = null
    private var refresh: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

//        val sharedPreferences = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        val savedToken = sessionManager.fetchAccessToken()
        val savedRefresh = sessionManager.fetchRefreshToken()

        if (savedToken != null && savedRefresh != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Finish SignInActivity to remove it from the back stack
            return
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                handleToken(url.toString(), view)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                handleToken(url.toString(), view)
            }
        }
        webView.loadUrl("https://filmix.tv/auth/login")  // Replace with the actual login URL
    }

    override fun onStop() {
        binding.webView.loadUrl("javascript:localStorage.clear()")
        Log.d(TAG, "onStop")
        super.onStop()
    }

    private fun handleToken(url: String, view: WebView?) {
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
        checkAndStoreTokens()
    }


    private fun checkAndStoreTokens() {
        if (token != null && refresh != null) {
            sessionManager.saveAccessToken(
                token!!,
                expiresIn = System.currentTimeMillis() + (50 * 60 * 1000)
            )
            sessionManager.saveRefreshToken(refresh!!)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
