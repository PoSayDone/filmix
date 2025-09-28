package io.github.posaydone.filmix.tv.ui.screen.profileScreen

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
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import io.github.posaydone.filmix.core.common.sharedViewModel.ProfileScreenUiState
import io.github.posaydone.filmix.core.common.sharedViewModel.ProfileScreenViewModel
import io.github.posaydone.filmix.tv.navigation.Screens
import io.github.posaydone.filmix.tv.ui.common.Error
import io.github.posaydone.filmix.tv.ui.common.LargeButton
import io.github.posaydone.filmix.tv.ui.common.LargeButtonStyle
import io.github.posaydone.filmix.tv.ui.common.Loading

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is ProfileScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is ProfileScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize(), onRetry = s.onRetry)
        }

        is ProfileScreenUiState.Success -> {
            ProfileScreenContent(
                userProfile = s.userProfile,
                onLogout = {
                    viewModel.logout()
                    navController.popBackStack() // Navigate back after logout
                },
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
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username
        Text(
            text = "@${userProfile.login}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        Text(
            text = userProfile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            style = MaterialTheme.typography.bodyMedium,
            color = if (userProfile.isPro || userProfile.isProPlus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        LargeButton(
            onClick = onLogout,
            style = LargeButtonStyle.FILLED
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = "Logout icon"
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}