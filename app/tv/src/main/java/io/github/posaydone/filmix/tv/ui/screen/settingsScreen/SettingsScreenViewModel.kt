package io.github.posaydone.filmix.tv.ui.screen.settingsScreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.data.SettingsManager
import io.github.posaydone.filmix.core.model.StreamTypeResponse
import io.github.posaydone.filmix.core.model.UserProfileInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: FilmixRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsScreenUiState>(SettingsScreenUiState.Loading)
    val uiState: StateFlow<SettingsScreenUiState> = _uiState

    private val _videoQuality = MutableStateFlow(settingsManager.getVideoQuality())
    val videoQuality: StateFlow<String> = _videoQuality

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = SettingsScreenUiState.Loading

        viewModelScope.launch {
            try {
                // Load stream type and user profile (for server info) concurrently
                val streamTypeResponse = repository.getStreamType()
                val userProfile = repository.getUserProfile()

                _uiState.value = SettingsScreenUiState.Success(
                    currentStreamType = streamTypeResponse.streamType,
                    allowedStreamTypes = streamTypeResponse.allowedTypes,
                    currentServerLocation = userProfile.server ?: "AUTO"
                )
                
                // Load video quality from settings manager
                _videoQuality.value = settingsManager.getVideoQuality()
            } catch (e: Exception) {
                _uiState.value = SettingsScreenUiState.Error(
                    message = e.message ?: "Unknown error",
                    onRetry = { loadSettings() }
                )
            }
        }
    }

    fun updateStreamType(newStreamType: String) {
        viewModelScope.launch {
            try {
                val success = repository.updateStreamType(newStreamType)
                if (success) {
                    val currentState = _uiState.value
                    if (currentState is SettingsScreenUiState.Success) {
                        _uiState.value = currentState.copy(currentStreamType = newStreamType)
                    }
                }
            } catch (e: Exception) {
                // Handle error - maybe revert the UI or show error message
            }
        }
    }

    fun updateServerLocation(newServerLocation: String) {
        viewModelScope.launch {
            try {
                val success = repository.updateServerLocation(newServerLocation)
                if (success) {
                    val currentState = _uiState.value
                    if (currentState is SettingsScreenUiState.Success) {
                        _uiState.value = currentState.copy(currentServerLocation = newServerLocation)
                    }
                }
            } catch (e: Exception) {
                // Handle error - maybe revert the UI or show error message
            }
        }
    }

    fun setVideoQuality(quality: String) {
        _videoQuality.value = quality
        settingsManager.setVideoQuality(quality)
    }

    companion object {
        val serverLocationMap = serverLocations
    }
}

@Immutable
sealed interface SettingsScreenUiState {
    data object Loading : SettingsScreenUiState
    data class Error(val message: String, val onRetry: () -> Unit) : SettingsScreenUiState
    data class Success(
        val currentStreamType: String,
        val allowedStreamTypes: List<String>,
        val currentServerLocation: String
    ) : SettingsScreenUiState
}

// List of available server locations
val serverLocations = mapOf(
    "AUTO" to "Auto",
    "VSSP" to "Russia (Saint-Petersburg)",
    "VRNL" to "Netherlands",
    "VSFR" to "France",
    "VSFI" to "Finland"
)
