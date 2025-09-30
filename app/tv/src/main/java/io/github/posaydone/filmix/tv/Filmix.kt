package io.github.posaydone.filmix.tv

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
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
                Log.d("FilmixTVApp", "TV App started, ready for update checks")
            } catch (e: Exception) {
                Log.e("FilmixTVApp", "Error during app initialization", e)
            }
        }
    }
}
