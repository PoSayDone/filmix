package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.common.service.AppUpdateInfo
import io.github.posaydone.filmix.core.data.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateManagerViewModel @Inject constructor(
    private val githubRepository: GithubRepository
) : ViewModel() {
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState
    
    private var applicationContext: android.content.Context? = null
    
    fun initializeContext(context: android.content.Context) {
        this.applicationContext = context
    }
    
    fun checkForUpdate() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            try {
                val updateInfo = performUpdateCheck()
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
    
    private suspend fun performUpdateCheck(): AppUpdateInfo? {
        val context = applicationContext ?: return null
        val result = githubRepository.getLatestRelease()
        
        if (result.isSuccess) {
            val release = result.getOrNull()
            if (release != null) {
                val currentVersionCode = getCurrentVersionCode(context)
                val latestVersionCode = extractVersionCodeFromTag(release.tagName)
                
                // Find the APK asset for the current platform (mobile or TV)
                val apkAsset = release.releaseAssets.find { 
                    it.name.endsWith(".apk") 
                }
                
                if (apkAsset != null && latestVersionCode > currentVersionCode) {
                    return AppUpdateInfo(
                        versionName = release.releaseName,
                        versionCode = latestVersionCode,
                        downloadUrl = apkAsset.downloadUrl,
                        changelog = release.body,
                        isUpdateAvailable = true,
                        releaseUrl = release.htmlUrl
                    )
                }
            }
        }
        
        return null
    }
    
    private fun getCurrentVersionCode(context: android.content.Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            androidx.core.content.pm.PackageInfoCompat.getLongVersionCode(packageInfo)
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            android.util.Log.e("UpdateManagerViewModel", "Package name not found", e)
            0L
        }
    }
    
    private fun extractVersionCodeFromTag(tag: String): Long {
        // Extract version numbers from tag like "v1.2.3" or "1.2.3"
        val pattern = java.util.regex.Pattern.compile("\\d+")
        val matcher = pattern.matcher(tag)
        var versionCode = 0L
        
        // Simple version to number conversion - in a real app you might want more sophisticated version comparison
        if (matcher.find()) {
            val major = matcher.group().toLongOrNull() ?: 0
            versionCode += major * 10000
            
            if (matcher.find()) {
                val minor = matcher.group().toLongOrNull() ?: 0
                versionCode += minor * 100
                
                if (matcher.find()) {
                    val patch = matcher.group().toLongOrNull() ?: 0
                    versionCode += patch
                }
            }
        }
        
        return versionCode
    }
    
    fun downloadAndInstallUpdate(context: android.content.Context) {
        val updateInfo = (_updateState.value as? UpdateState.UpdateAvailable)?.updateInfo
        if (updateInfo != null) {
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(updateInfo.downloadUrl)
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                android.util.Log.e("UpdateManagerViewModel", "Error opening download URL", e)
            }
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