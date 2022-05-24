package com.chemist.youngchemist.ui.util

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
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.R
import com.chemist.youngchemist.model.ui.LectureUi
import com.chemist.youngchemist.model.user.UserProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.text.DecimalFormat

fun View.showMessage(context: Context, animTime: Long) {
    val enterAnim = AnimationUtils.loadAnimation(context, R.anim.result_message_anim_enter).apply {
        duration = animTime
    }
    this.startAnimation(enterAnim)
}

fun <T> Flow<T>.toStateFlow(initialValue: T,scope: CoroutineScope): StateFlow<T> {
    return this.stateIn(scope, SharingStarted.Lazily, initialValue)
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

fun Long.evaluateTime(): String {
    val minutes = this / 1000 / 60
    val seconds = ((this / 1000) % 60)
    return "${if (minutes < 10) "0$minutes" else minutes}" + ":" +
            "${if (seconds < 10) "0$seconds" else seconds}"

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