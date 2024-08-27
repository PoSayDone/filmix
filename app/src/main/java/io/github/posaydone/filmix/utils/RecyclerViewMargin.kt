package io.github.posaydone.filmix.utils
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
class RecyclerViewMargin(
    @IntRange(from = 0) private val margin: Int,
    @IntRange(from = 0) private val columns: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        val column = position % columns

        outRect.left = column * margin / columns; // column * ((1f / spanCount) * spacing)
        outRect.right = margin - (column + 1) * margin / columns; // spacing - (column + 1) * ((1f /    spanCount) * spacing)

        if (position >= columns) {
            outRect.top = margin; // item top
        }
    }
}
