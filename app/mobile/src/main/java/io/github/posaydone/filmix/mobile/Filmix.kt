package io.github.posaydone.filmix.mobile

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.github.posaydone.filmix.core.common.viewmodel.UpdateManagerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class Filmix : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize auto-update check on app startup
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Update manager will be initialized when needed in the activity
                Log.d("FilmixApp", "App started, ready for update checks")
            } catch (e: Exception) {
                Log.e("FilmixApp", "Error during app initialization", e)
            }
        }
    }
}
