package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.AuthRepository
import io.github.posaydone.filmix.core.model.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed class AuthScreenUiState {
    data object Idle : AuthScreenUiState()
    data object Loading : AuthScreenUiState()
    data object Success : AuthScreenUiState()
    data class Error(val message: String) : AuthScreenUiState()
}

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthScreenUiState>(AuthScreenUiState.Idle)
    val uiState: StateFlow<AuthScreenUiState> = _uiState

    fun authorizeUser(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenUiState.Loading
            try {

                val response = withContext(Dispatchers.IO) {
                    authRepository.login(username, password)
                }

                sessionManager.saveAccessToken(
                    response.access_token,
                    System.currentTimeMillis() + 50 * 60 * 1000
                )
                sessionManager.saveRefreshToken(response.refresh_token)

                _uiState.value = AuthScreenUiState.Success;

            } catch (e: Exception) {
                _uiState.value =
                    AuthScreenUiState.Error(e.message.toString())
            }

        }
    }

    fun onNavigationHandled() {
        _uiState.value = AuthScreenUiState.Idle
    }

}
