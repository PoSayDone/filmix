package io.github.posaydone.filmix.ui.views

import android.content.Context
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog

class FullscreenBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    override fun onStart() {
        super.onStart()
        val activityInsetsController =
            WindowCompat.getInsetsController(window!!, window!!.decorView)
        activityInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}