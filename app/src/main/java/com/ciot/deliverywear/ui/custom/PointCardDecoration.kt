package com.ciot.deliverywear.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PointCardDecoration(private val verticalSpaceHeight: Int, private val horizontalSpaceWidth: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (position % 2 == 0) {
            outRect.left = horizontalSpaceWidth
            outRect.right = 4
        } else {
            outRect.left = 4
            outRect.right = horizontalSpaceWidth
        }
        outRect.top = verticalSpaceHeight
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}
