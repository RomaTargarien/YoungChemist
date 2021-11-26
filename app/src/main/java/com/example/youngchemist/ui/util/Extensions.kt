package com.example.youngchemist.ui.util

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.youngchemist.R
import java.nio.ByteBuffer

val String.Companion.EMPTY: String get() = ""

fun View.showMessage(context: Context, animTime: Long) {
    val enterAnim = AnimationUtils.loadAnimation(context, R.anim.result_message_anim_enter).apply {
        duration = animTime
    }
    this.startAnimation(enterAnim)
}

fun View.hideMessage(context: Context,animTime: Long): Animation {
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

fun Fragment.closeKeyBoard() {
    val view = this.activity?.currentFocus
    view?.let {
        val imm = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }
}

fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun String.GLB() = this + ".glb"

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    getSystem().displayMetrics)