package io.github.posaydone.filmix.core.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.network.service.GithubApiService
import io.github.posaydone.filmix.mobile.utils.AppUpdateInfo
import io.github.posaydone.filmix.mobile.utils.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateManagerViewModel @Inject constructor(
    private val githubApiService: GithubApiService
) : ViewModel() {
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState
    
    private var appUpdateManager: AppUpdateManager? = null
    
    fun initializeUpdateManager(applicationContext: android.content.Context) {
        appUpdateManager = AppUpdateManager(applicationContext, githubApiService)
    }
    
    fun checkForUpdate() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            try {
                val updateInfo = appUpdateManager?.checkForUpdate()
                if (updateInfo != null && updateInfo.isUpdateAvailable) {
                    _updateState.value = UpdateState.UpdateAvailable(updateInfo)
                } else {
                    _updateState.value = UpdateState.NoUpdateFound
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun downloadAndInstallUpdate() {
        val updateInfo = (_updateState.value as? UpdateState.UpdateAvailable)?.updateInfo
        if (updateInfo != null) {
            // This would be called from an Activity context
            // Implementation would happen in the calling Activity
        }
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class UpdateAvailable(val updateInfo: AppUpdateInfo) : UpdateState()
    object NoUpdateFound : UpdateState()
    data class Error(val message: String) : UpdateState()
}