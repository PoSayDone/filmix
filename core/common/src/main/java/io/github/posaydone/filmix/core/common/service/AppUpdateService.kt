package io.github.posaydone.filmix.core.common.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import io.github.posaydone.filmix.core.common.sharedViewModel.UpdateManagerViewModel
import io.github.posaydone.filmix.core.common.sharedViewModel.UpdateState

class AppUpdateService(
    private val context: Context,
    private val updateManagerViewModel: UpdateManagerViewModel
) {
    
    companion object {
        private const val TAG = "AppUpdateService"
    }
    
    init {
        updateManagerViewModel.initializeContext(context)
    }
    
    fun observeUpdateState(owner: LifecycleOwner, onStateChanged: (UpdateState) -> Unit) {
        updateManagerViewModel.updateState.observe(owner) { state ->
            onStateChanged(state)
        }
    }
    
    fun checkForUpdate() {
        updateManagerViewModel.checkForUpdate()
    }
    
    fun downloadAndInstallUpdate() {
        updateManagerViewModel.downloadAndInstallUpdate(context)
    }
    
    fun getLatestUpdateInfo(): AppUpdateInfo? {
        return (updateManagerViewModel.updateState.value as? UpdateState.UpdateAvailable)?.updateInfo
    }
    
    fun isUpdateAvailable(): Boolean {
        return updateManagerViewModel.updateState.value is UpdateState.UpdateAvailable
    }
    
    fun autoCheckForUpdate() {
        checkForUpdate()
    }
}

data class AppUpdateInfo(
    val versionName: String,
    val versionCode: Long,
    val downloadUrl: String,
    val changelog: String,
    val isUpdateAvailable: Boolean,
    val releaseUrl: String
)

