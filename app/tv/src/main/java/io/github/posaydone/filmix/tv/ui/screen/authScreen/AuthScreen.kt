package io.github.posaydone.filmix.tv.ui.screen.authScreen

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.common.sharedViewModel.AuthScreenViewModel
import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.TextField
import kotlinx.coroutines.launch

private var TAG = "AuthScreen"
private var access: String? = null
private var refresh: String? = null
private var hash: String? = null

fun decodeBase64Svg(base64String: String): ByteArray {
    // Remove the data URI prefix if present
    val cleanBase64 = base64String.replace("data:image/svg+xml;base64,", "")
    return Base64.decode(cleanBase64, Base64.DEFAULT)
}

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navigateToHome = {
        navController.navigate(Screens.Main) {
            popUpTo<Screens.Auth> { inclusive = true }
        }
    }

    val scope = rememberCoroutineScope()
    var hash by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var qrCode by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (viewModel.areTokensSaved()) {
        navigateToHome()
    }

    LaunchedEffect(key1 = Unit) { // Runs once when AuthScreen enters composition
        // --- Improvement: Check token status once as an effect ---
        if (viewModel.areTokensSaved()) {
            Log.d("AuthScreen", "Tokens already saved, navigating to home.")
            navigateToHome()
        } else {
            // --- Fetch hash only if not already logged in ---
            Log.d("AuthScreen", "Tokens not saved, requesting hash.")
            loading = true
            errorMessage = null // Clear previous errors
            try {
                // Directly call the suspend function
                val fetchedHash = viewModel.requestHash()
                hash = fetchedHash.token // Update state AFTER the suspend function completes

                // You might generate the QR code here if it depends on the hash
                qrCode = viewModel.requestQrCode(hash).image

                Log.d("AuthScreen", "Successfully fetched hash: $fetchedHash")

            } catch (e: Exception) {
                Log.e("AuthScreen", "Error fetching hash", e)
                errorMessage = "Failed to initialize authentication: ${e.message}"
                // Decide what to do with hash on error, maybe clear it?
                hash = ""
            } finally {
                // Ensure loading is set to false regardless of success/failure
                loading = false
                Log.d("AuthScreen", "Finished hash request attempt.")
            }
        }
    }


    Row {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(32.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Sign in", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(18.dp))
            TextField(value = email, onValueChange = { email = it }, placeholderText = "Email")
            Spacer(Modifier.height(12.dp))
            TextField(
                value = password, onValueChange = { password = it }, placeholderText = "Password"
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        errorMessage = null
                        try {
                            // Step 2: Authorize
                            val authResponse = viewModel.authorizeUser(
                                hash = hash, body = AuthRequestBody(
                                    user_name = email, user_passw = password, session = true
                                )
                            )

                            viewModel.saveTokens(
                                access = authResponse.accessToken,
                                refresh = authResponse.refreshToken,
                                hash = hash,
                                expiresInMs = 50 * 60 * 1000
                            )

                            navigateToHome()
                        } catch (e: Exception) {
                            errorMessage = "Authorization failed: ${e.localizedMessage}"
                        } finally {
                            loading = false
                        }
                    }
                }, enabled = !loading && hash.isNotEmpty()
            ) { Text("Login") }
        }
        Spacer(Modifier.width(32.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (qrCode.isNotEmpty()) {
                val byteArray = decodeBase64Svg(qrCode)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12))
                        .background(Color.White)
                        .size(300.dp, 300.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(byteArray)
                            .decoderFactory(SvgDecoder.Factory()).build(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(12.dp)
                    )
                }
            }
        }

    }
}

