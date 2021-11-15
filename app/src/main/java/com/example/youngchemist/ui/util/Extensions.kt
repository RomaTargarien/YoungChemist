package com.example.youngchemist.ui.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.youngchemist.R

val String.Companion.EMPTY: String get() = ""

fun View.showErrorMessage(context: Context, animTime: Long) {
    val enterAnim = AnimationUtils.loadAnimation(context, R.anim.result_message_anim_enter).apply {
        duration = animTime
    }
    this.startAnimation(enterAnim)
}

fun View.hideErrorMessage(context: Context,animTime: Long): Animation {
    val exitAnim = AnimationUtils.loadAnimation(context,R.anim.result_message_anim_exit).apply {
        duration = animTime
    }
    this.startAnimation(exitAnim)
    return exitAnim
}

fun View.updateAlpha(context: Context,animTime: Long): Animation {
    val alphaAnim = AnimationUtils.loadAnimation(context,R.anim.alpha_anim).apply {
        duration = animTime
    }
    this.startAnimation(alphaAnim)
    return alphaAnim
}

fun View.slideUp(context: Context, anitTime: Long, startOffSet: Long) {

    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_anim).apply {
        duration = anitTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffSet
    }
    this.startAnimation(slideUp)
}

fun Fragment.slideUpViews(vararg views: View, animTime: Long = 300L, delay: Long = 150L) {
    for (i in views.indices) {
        views[i].slideUp(this.requireContext(),animTime,delay * i)
    }
}