package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalItemVerticalDecoration(val rightSpace: Int,val leftSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = rightSpace
        outRect.left = leftSpace
    }
}