package kz.narrator.app.utils.rv

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import kz.narrator.app.R

class MarginItemDecoration(
    val context: Context,
    @DimenRes val leftDimen: Int = R.dimen.dp_0,
    @DimenRes val topDimen: Int = R.dimen.dp_0,
    @DimenRes val rightDimen: Int = R.dimen.dp_0,
    @DimenRes val bottomDimen: Int = R.dimen.dp_0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            left = context.resources.getDimensionPixelSize(leftDimen)
            right = context.resources.getDimensionPixelSize(rightDimen)

            if (parent.getChildAdapterPosition(view) == 0) {
                top = context.resources.getDimensionPixelSize(topDimen)
            }
            bottom = context.resources.getDimensionPixelSize(bottomDimen)
        }
    }
}