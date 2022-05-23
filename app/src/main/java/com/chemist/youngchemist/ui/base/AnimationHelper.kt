package com.chemist.youngchemist.ui.base

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.chemist.youngchemist.R
import com.chemist.youngchemist.ui.util.hideMessage
import com.chemist.youngchemist.ui.util.showMessage
import com.chemist.youngchemist.ui.util.updateAlpha

class AnimationHelper(
    private val context: Context,
    private val container: ViewGroup,
    private val errorMessageView: View,
    private val errorMessageTextView: TextView,
    private val buttonToDisable: Button,
    private val resultImage: ImageView? = null,
    private val ooopsTextView: TextView? = null
) {

    fun showMessage(message: String, itIsErrorMessage: Boolean = true) {
        if (!itIsErrorMessage) {
            resultImage?.setImageResource(R.drawable.ic_icon_happy_flask)
            ooopsTextView?.isVisible = false
        } else {
            resultImage?.setImageResource(R.drawable.ic_icon_sad_flask)
            ooopsTextView?.isVisible = true
        }
        errorMessageTextView.setText(message)
        errorMessageView.isVisible = true
        errorMessageView.showMessage(context, 250)
    }

    fun hideMessage() {
        errorMessageView.hideMessage(context, 1000)
            .setAnimationListener(hideMessageAnimationListener)
    }

    fun animateProgressBar(progressBar: ProgressBar, from: Float, to: Float) {
        val pregressAnimator =
            ObjectAnimator.ofInt(progressBar, "progress", from.toInt(), to.toInt())
        pregressAnimator.duration = 1000
        pregressAnimator.start()
    }

    private val hideMessageAnimationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            container.updateAlpha(context, 1000).setAnimationListener(updateAlphaListner)
        }

        override fun onAnimationEnd(p0: Animation?) {
            errorMessageView.isVisible = false
            buttonToDisable.isEnabled = true
        }

        override fun onAnimationRepeat(p0: Animation?) {}
    }
    private val updateAlphaListner = object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            container.alpha = 1f
        }

        override fun onAnimationEnd(p0: Animation?) {}
        override fun onAnimationRepeat(p0: Animation?) {}
    }
}