package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.repository.AuthRepository
import io.github.posaydone.filmix.core.data.repository.FilmixRepository
import io.github.posaydone.filmix.core.model.UserProfileInfo
import io.github.posaydone.filmix.core.model.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface ProfileScreenUiState {
    data object Loading : ProfileScreenUiState
    data class Error(val message: String, val onRetry: () -> Unit) : ProfileScreenUiState
    data class Success(val userProfile: UserProfileInfo) : ProfileScreenUiState
}

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repository: FilmixRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Loading)
    val uiState: StateFlow<ProfileScreenUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.value = ProfileScreenUiState.Loading
        
        viewModelScope.launch {
            try {
                val userProfile = repository.getUserProfile()
                _uiState.value = ProfileScreenUiState.Success(userProfile)
            } catch (e: Exception) {
                _uiState.value = ProfileScreenUiState.Error(
                    message = e.message ?: "Unknown error",
                    onRetry = { loadUserProfile() }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // Call the backend logout endpoint first
                authRepository.logout()
            } catch (e: Exception) {
                // Even if the backend logout fails, we still want to clear the local session
            }
            // Clear the local session
            sessionManager.logout()
        }
    }
}