package io.github.posaydone.filmix.tv.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import io.github.posaydone.filmix.core.model.GithubRelease
import io.github.posaydone.filmix.core.model.ReleaseAsset
import io.github.posaydone.filmix.core.network.service.GithubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

class AppUpdateManager(
    private val context: Context,
    private val githubApiService: GithubApiService
) {
    
    companion object {
        private const val TAG = "AppUpdateManager"
    }
    
    suspend fun checkForUpdate(): AppUpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val response = githubApiService.getLatestRelease()
            
            if (response.isSuccessful) {
                val release = response.body()
                if (release != null) {
                    val currentVersionCode = getCurrentVersionCode()
                    val latestVersionCode = extractVersionCodeFromTag(release.tagName)
                    
                    // Find the APK asset for TV
                    val apkAsset = release.releaseAssets.find { 
                        it.name.endsWith(".apk") && it.name.contains("tv", ignoreCase = true) 
                    }
                    
                    if (apkAsset != null && latestVersionCode > currentVersionCode) {
                        return@withContext AppUpdateInfo(
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
            
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for update", e)
            return@withContext null
        }
    }
    
    private fun getCurrentVersionCode(): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            PackageInfoCompat.getLongVersionCode(packageInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package name not found", e)
            0L
        }
    }
    
    private fun extractVersionCodeFromTag(tag: String): Long {
        // Extract version numbers from tag like "v1.2.3" or "1.2.3"
        val pattern = Pattern.compile("\\d+")
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
    
    fun downloadAndInstallUpdate(activity: Activity, downloadUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(downloadUrl)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening download URL", e)
        }
    }
    
    suspend fun autoCheckForUpdate(): Boolean = withContext(Dispatchers.IO) {
        val updateInfo = checkForUpdate()
        return@withContext updateInfo != null && updateInfo.isUpdateAvailable
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