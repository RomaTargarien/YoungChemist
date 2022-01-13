package com.example.youngchemist.ui.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.youngchemist.R
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.user.UserProgress
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.text.DecimalFormat

val String.Companion.EMPTY: String get() = ""

fun View.showMessage(context: Context, animTime: Long) {
    val enterAnim = AnimationUtils.loadAnimation(context, R.anim.result_message_anim_enter).apply {
        duration = animTime
    }
    this.startAnimation(enterAnim)
}

fun View.hideMessage(context: Context, animTime: Long): Animation {
    val exitAnim = AnimationUtils.loadAnimation(context, R.anim.result_message_anim_exit).apply {
        duration = animTime
    }
    this.startAnimation(exitAnim)
    return exitAnim
}

fun View.updateAlpha(context: Context, animTime: Long): Animation {
    val alphaAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_anim).apply {
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
        views[i].slideUp(this.requireContext(), animTime, delay * i)
    }
}

fun Fragment.closeKeyBoard() {
    val view = this.activity?.currentFocus
    view?.let {
        val imm =
            this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()

fun Collection<LectureUi>.getLecturesId(): List<String> {
    val lecturesId = mutableListOf<String>()
    for (item in this) {
        lecturesId.add(item.lectureId)
    }
    return lecturesId
}

fun Collection<UserProgress>.getLocalLecturesId(): List<String> {
    val lecturesId = mutableListOf<String>()
    for (item in this) {
        lecturesId.add(item.lectureId)
    }
    return lecturesId
}

fun Long.evaluateTime(): String {
    val minutes = this / 1000 / 60
    val seconds = ((this / 1000) % 60)
    return "${if (minutes < 10) "0$minutes" else minutes}" + ":" +
            "${if (seconds < 10) "0$seconds" else seconds}"

}

fun View.bounce(): AnimatorSet {
    val animatorSet = AnimatorSet()

    val object1: ObjectAnimator =
        ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.25f, 0.75f, 1.15f, 1f)
    val object2: ObjectAnimator =
        ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.75f, 1.25f, 0.85f, 1f)

    animatorSet.playTogether(object1, object2)
    return animatorSet
}

fun ProgressBar.animateProgress(from: Int, to: Int) {
    val pregressAnimator = ObjectAnimator.ofInt(this, "progress", from, to)
    pregressAnimator.duration = 1800
    pregressAnimator.start()
}

fun View.shake(): AnimatorSet {
    val animatorSet = AnimatorSet()
    val objectAnimator: ObjectAnimator =
        ObjectAnimator.ofFloat(this, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
    animatorSet.playTogether(objectAnimator)
    return animatorSet
}