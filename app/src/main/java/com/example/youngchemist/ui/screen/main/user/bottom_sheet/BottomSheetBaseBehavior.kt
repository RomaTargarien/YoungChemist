package com.example.youngchemist.ui.screen.main.user.bottom_sheet

import android.animation.LayoutTransition
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.example.youngchemist.R

abstract class BottomSheetBaseBehavior : BottomSheetBase {

    fun toggleTextViewBehavior(textView: TextView, enabled: Boolean) {
        textView.alpha = if (enabled) 1.0f else 0.5f
        textView.isEnabled = enabled
    }

    fun toggleErrorMessageBehavior(
        container: ConstraintLayout,
        errorTextView: TextView,
        errorText: String?
    ) {
        if (errorText != null) {
            TransitionManager.beginDelayedTransition(container)
            errorTextView.isVisible = true
            errorTextView.text = errorText
        } else {
            if (errorTextView.isVisible) {
                TransitionManager.beginDelayedTransition(container)
                errorTextView.isVisible = false
            }
        }
    }

    fun togglePasswordVisibility(
        passwordEditText: EditText,
        passwordVisibilityImage: ImageView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passwordEditText.setSelection(passwordEditText.text.length)
            passwordVisibilityImage.setImageResource(R.drawable.ic_icon_view_password)
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordEditText.setSelection(passwordEditText.text.length)
            passwordVisibilityImage.setImageResource(R.drawable.ic_icon_hide_password)
        }
    }

    fun disableViews(container: ConstraintLayout, textView: TextView) {
        container.alpha = 0.5f
        textView.alpha = 0.5f
        textView.isEnabled = false
        container.children.forEach {
            it.alpha = 0.5f
            it.isEnabled = false
        }
    }

    fun enableViews(container: ConstraintLayout, textView: TextView) {
        container.alpha = 1f
        textView.alpha = 1f
        textView.isEnabled = true
        container.children.forEach {
            it.alpha = 1f
            it.isEnabled = true
        }
    }

    fun toggleContainerTransition(container: ConstraintLayout, topMargin: Int) {
        val params = container.layoutParams as ViewGroup.MarginLayoutParams
        (container as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        params.topMargin = topMargin
        container.layoutParams = params
    }
}

interface BottomSheetBase {
    fun init(viewModel: BottomSheetViewModelBase)
    fun subscribeToObservers()
    fun removeObservers()
    fun setOnDataHasChangedListener(listener: (String) -> Unit)
}