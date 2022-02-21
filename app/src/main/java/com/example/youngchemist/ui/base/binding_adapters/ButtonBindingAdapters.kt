package com.example.youngchemist.ui.base.binding_adapters

import android.widget.Button
import androidx.databinding.BindingAdapter

@BindingAdapter("enableButton")
fun Button.enableButton(isButtonEnabled: Boolean) {
    isEnabled = isButtonEnabled
    alpha = if (isButtonEnabled) 1f else 0.7f
}