package com.example.youngchemist.ui.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.youngchemist.ui.util.hideErrorMessage
import com.example.youngchemist.ui.util.showErrorMessage
import com.example.youngchemist.ui.util.updateAlpha

class AnimationHelper(
    private val context: Context,
    private val container: ViewGroup,
    private val errorMessageView: View,
    private val errorMessageTextView: TextView,
    private val buttonToDisable: Button
) {

    fun showErrorMessage(errorText: String) {
        errorMessageTextView.setText(errorText)
        errorMessageView.isVisible = true
        errorMessageView.showErrorMessage(context,250)
    }
    fun hideErrorMessage() {
        errorMessageView.hideErrorMessage(context,1000).setAnimationListener(hideErrorMessageAnimationListener)
    }

    private val hideErrorMessageAnimationListener = object : Animation.AnimationListener {
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