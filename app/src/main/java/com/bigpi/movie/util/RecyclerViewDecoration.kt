package com.bigpi.movie.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewDecoration(
    private val paddingFirstItemHeight: Float,
    private val height: Float,
    private val padding: Float,
    @ColorInt
    private val color: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        paint.color = color
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (itemPosition == 0) {
            outRect.top = paddingFirstItemHeight.toInt()
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val left = parent.paddingStart + padding
        val right = parent.width - parent.paddingEnd - padding

        for (i in 0..parent.childCount - 2) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams



            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height

            c.drawRect(left, top, right, bottom, paint)
        }
    }
}