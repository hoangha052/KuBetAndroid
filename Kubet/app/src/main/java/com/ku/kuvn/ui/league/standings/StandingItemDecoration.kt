package com.ku.kuvn.ui.league.standings

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StandingItemDecoration(private val offset: Int,
                             private val divider: Drawable?) : RecyclerView.ItemDecoration() {

    private val mBounds = Rect()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val childAdapterPosition = parent.getChildAdapterPosition(view)
        val standingsAdapter = parent.adapter as? StandingsAdapter

        if (standingsAdapter != null && childAdapterPosition > 0) {
            val item = standingsAdapter.getItem(childAdapterPosition)
            if (item is GroupItem) {
                outRect.set(0, offset, 0, 0)
            } else if (divider != null) {
                outRect.set(divider.intrinsicWidth, 0, divider.intrinsicWidth, divider.intrinsicHeight)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || divider == null) {
            return
        }
        val standingsAdapter = parent.adapter as? StandingsAdapter ?: return
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val adapterPos = parent.getChildAdapterPosition(child)
            val item = standingsAdapter.getItem(adapterPos)

            if (item == null || item is GroupItem) {
                continue
            }
            parent.getDecoratedBoundsWithMargins(child, mBounds)

            // left divider
            divider.setBounds(mBounds.left, mBounds.top, mBounds.left + divider.intrinsicWidth, mBounds.bottom)
            divider.draw(c)

            // right divider
            divider.setBounds(mBounds.right - divider.intrinsicWidth, mBounds.top, mBounds.right, mBounds.bottom)
            divider.draw(c)

            // bottom divider
            divider.setBounds(mBounds.left + divider.intrinsicWidth,
                    mBounds.bottom - divider.intrinsicHeight,
                    mBounds.right - divider.intrinsicWidth, mBounds.bottom)
            divider.draw(c)
        }
    }
}