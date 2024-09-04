package io.github.posaydone.filmix.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class CustomPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {

    private val TAG: String = "player"
    private var scaleGestureDetector: ScaleGestureDetector
    private var isScaling: Boolean = false

    init {
        scaleGestureDetector = ScaleGestureDetector(context, CustomOnScaleGestureListener(this))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        if (!isScaling) {
            return super.performClick()
        } else {
            isScaling = false
            return true
        }
    }


    private inner class CustomOnScaleGestureListener(
        private val player: PlayerView
    ) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor = 0f

        override fun onScale(
            detector: ScaleGestureDetector
        ): Boolean {
            scaleFactor = detector.scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            return true
        }

        @OptIn(UnstableApi::class)
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            if (scaleFactor > 1) {
                player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            } else {
                player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        }
    }

}