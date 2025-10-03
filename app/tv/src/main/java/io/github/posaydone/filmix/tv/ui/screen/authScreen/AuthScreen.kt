package io.github.posaydone.filmix.tv.ui.screen.authScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.Icon
import io.github.posaydone.filmix.tv.ui.common.LargeButton
import io.github.posaydone.filmix.tv.ui.common.LargeButtonStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.posaydone.filmix.core.common.sharedViewModel.AuthScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.AuthScreenViewModel
import io.github.posaydone.filmix.core.common.R
import io.github.posaydone.filmix.tv.ui.common.PasswordTextField
import io.github.posaydone.filmix.tv.ui.common.TextField

private var TAG = "AuthScreen"

@Composable
fun AuthScreen(
    navigateToHome: () -> Unit,
    viewModel: AuthScreenViewModel = hiltViewModel(),
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = uiState) {
        if (uiState is AuthScreenUiState.Success) {
            navigateToHome()
            viewModel.onNavigationHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign in", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(18.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholderText = "Email",
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier.height(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            placeholderText = "Password",
            contentPadding = PaddingValues(start = 24.dp, end = 12.dp),
            modifier = Modifier.height(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        LargeButton(
            onClick = {
                viewModel.authorizeUser(username = email, password = password)
            }, 
            enabled = uiState != AuthScreenUiState.Loading,
            style = LargeButtonStyle.FILLED
        ) {
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.size(12.dp))
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Rounded.ArrowRightAlt,
                contentDescription = null
            )
        }
        if (uiState is AuthScreenUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as AuthScreenUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

