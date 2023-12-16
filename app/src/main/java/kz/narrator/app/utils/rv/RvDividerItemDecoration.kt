package kz.narrator.app.utils.rv

import android.content.Context
import android.graphics.drawable.InsetDrawable
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.DividerItemDecoration
import kz.narrator.app.R

//class RvDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
//
//    private val mDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.shape_rv_line_divider)
//
//    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        val left = parent.paddingLeft
//        val right = parent.width - parent.paddingRight
//
//        val childCount = parent.childCount
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//
//            val params = child.layoutParams as RecyclerView.LayoutParams
//
//            val top = child.bottom + params.bottomMargin
//            val bottom = top + mDivider!!.intrinsicHeight
//
//            mDivider.setBounds(left, top, right, bottom)
//            mDivider.draw(c)
//        }
//    }
//}

fun getDividerItemDecoration(
    context: Context,
    @DimenRes left: Int = R.dimen.dp_0,
    @DimenRes top: Int = R.dimen.dp_0,
    @DimenRes right: Int = R.dimen.dp_0,
    @DimenRes bottom: Int = R.dimen.dp_0
): DividerItemDecoration {

    val attrs = intArrayOf(android.R.attr.listDivider)

    val a = context.obtainStyledAttributes(attrs)
    val divider = a.getDrawable(0)

    val insetLeft = context.resources.getDimensionPixelSize(left)
    val insetTop = context.resources.getDimensionPixelSize(top)
    val insetRight = context.resources.getDimensionPixelSize(right)
    val insetBottom = context.resources.getDimensionPixelSize(bottom)

    val insetDivider = InsetDrawable(divider, insetLeft, insetTop, insetRight, insetBottom)
    a.recycle()

    val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    itemDecoration.setDrawable(insetDivider)

    return itemDecoration
}