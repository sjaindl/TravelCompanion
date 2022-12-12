package com.sjaindl.travelcompanion.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomDividerItemDecoration(
    context: Context,
    resId: Int,
    private val leftMargin: Int = 0,
    private val rightMargin: Int = 0
) : RecyclerView.ItemDecoration() {
    private var divider: Drawable = ContextCompat.getDrawable(context, resId)!!

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        val dividerLeftMargin = leftMargin
        val dividerRightMargin = parent.width - rightMargin

        for (index in 0 until parent.childCount) {

            // Don't render divider for last item
            if (index != parent.childCount - 1) {
                val child: View = parent.getChildAt(index)

                val params = child.layoutParams as RecyclerView.LayoutParams

                // calculating the distance of the divider to be drawn from the top
                val dividerTop = child.bottom + params.bottomMargin - 12
                val dividerBottom = dividerTop + divider.intrinsicHeight

                divider.setBounds(dividerLeftMargin, dividerTop, dividerRightMargin, dividerBottom)
                divider.draw(canvas)
            }
        }
    }
}