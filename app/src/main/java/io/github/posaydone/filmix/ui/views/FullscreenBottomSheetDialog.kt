package io.github.posaydone.filmix.ui.views

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog

class FullscreenBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.dismissWithAnimation = true
    }

}