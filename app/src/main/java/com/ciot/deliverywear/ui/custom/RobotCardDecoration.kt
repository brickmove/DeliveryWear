package com.ciot.deliverywear.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RobotCardDecoration(private val verticalSpaceHeight: Int, private val horizontalSpaceWidth: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (position != 0) { // Skip the first item
            outRect.top = verticalSpaceHeight
        }

        outRect.left = horizontalSpaceWidth
        outRect.right = horizontalSpaceWidth

        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}
