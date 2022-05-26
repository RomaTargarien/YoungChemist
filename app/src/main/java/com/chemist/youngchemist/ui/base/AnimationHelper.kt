package com.chemist.youngchemist.ui.base

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.chemist.youngchemist.R
import com.chemist.youngchemist.ui.util.hideMessage
import com.chemist.youngchemist.ui.util.showMessage

class AnimationHelper(
    private val context: Context,
    private val messageView: View,
    private val messageTextView: TextView,
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
        messageTextView.text = message
        messageView.isVisible = true
        messageView.showMessage(context, 250)
    }

    fun hideMessage() {
        messageView.hideMessage(context, 1000)
            .setAnimationListener(hideMessageAnimationListener)
    }

    private val hideMessageAnimationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}

        override fun onAnimationEnd(p0: Animation?) {
            messageView.isVisible = false
        }

        override fun onAnimationRepeat(p0: Animation?) {}
    }
}