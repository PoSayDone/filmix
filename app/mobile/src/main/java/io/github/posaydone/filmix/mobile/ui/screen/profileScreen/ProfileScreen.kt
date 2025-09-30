package io.github.posaydone.filmix.mobile.ui.screen.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.github.posaydone.filmix.core.common.sharedViewModel.ProfileScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ProfileScreenViewModel
import io.github.posaydone.filmix.mobile.ui.common.LargeButton

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is ProfileScreenUiState.Loading -> {
            // You can implement a loading indicator for mobile if needed
        }

        is ProfileScreenUiState.Error -> {
            // You can implement an error message for mobile if needed
        }

        is ProfileScreenUiState.Success -> {
            ProfileScreenContent(
                userProfile = s.userProfile,
                onLogout = { viewModel.logout() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ProfileScreenContent(
    userProfile: io.github.posaydone.filmix.core.model.UserProfileInfo,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // User avatar
        val painter = rememberAsyncImagePainter(userProfile.avatar ?: "")
        Image(
            painter = painter,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display name
        Text(
            text = userProfile.displayName ?: userProfile.login,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username
        Text(
            text = "@${userProfile.login}",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        Text(
            text = userProfile.email,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pro status
        val proStatus = if (userProfile.isProPlus) {
            "Pro+ (Days left: ${userProfile.proDaysLeft})"
        } else if (userProfile.isPro) {
            "Pro (Days left: ${userProfile.proDaysLeft})"
        } else {
            "Free Account"
        }

        Text(
            text = proStatus,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Logout button
        LargeButton(
            onClick = onLogout
        ) {
            Icon(
                contentDescription = "Logout icon",
                imageVector = Icons.AutoMirrored.Rounded.Logout
            )
            Spacer(Modifier.size(12.dp))
            Text(text = "Logout")
        }
    }
}