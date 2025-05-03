package io.github.posaydone.filmix.mobile.ui.screen.authScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.core.common.sharedViewModel.AuthScreenViewModel
import io.github.posaydone.filmix.core.model.AuthRequestBody
import io.github.posaydone.filmix.mobile.navigation.Screens
import io.github.posaydone.filmix.mobile.ui.common.PasswordTextField
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel(),
) {
    val navigateToHome = {
        navController.navigate(Screens.Main) {
            popUpTo<Screens.Auth> { inclusive = true }
        }
    }

    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (viewModel.areTokensSaved()) {
        navigateToHome()
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            modifier = Modifier.size(60.dp),
            painter = painterResource(id = R.drawable.ic_filmix),
            contentDescription = "Filmix icon",
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,

                imeAction = ImeAction.Done
            ),

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth(),
            onClick = {
                scope.launch {
                    loading = true
                    errorMessage = null
                    try {
                        // Step 1: Request hash token
                        val hashResponse = viewModel.requestHash()
                        val hash = hashResponse.token

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
            }, enabled = !loading
        ) {
            Text(text = if (loading) "Loading..." else "Login", fontSize = 18.sp)
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
