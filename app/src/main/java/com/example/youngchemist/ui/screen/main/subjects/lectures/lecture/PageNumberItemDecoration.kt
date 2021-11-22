package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.ui.util.toDp

class PageNumberItemDecoration(val position: Int,val isDecorationShown: Boolean,val isAlphaToggledForAll: Boolean = false) : RecyclerView.ItemDecoration(){


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isAlphaToggledForAll) {
            val itemCount = state.itemCount
            for (i in 0..itemCount) {
                parent.getChildAt(i)?.alpha = 0.5f
            }
        }
        val view2 = parent.getChildAt(position)
        view2?.let {
            if (isDecorationShown) {
                it.alpha = 1f
            } else {
                it.alpha = 0.5f
            }

        }

    }
}