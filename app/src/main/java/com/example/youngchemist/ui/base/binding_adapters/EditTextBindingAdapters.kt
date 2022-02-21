package com.example.youngchemist.ui.base.binding_adapters

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("setPasswordVisibility")
fun EditText.setPasswordVisibility(isVisible: Boolean) {
    if (isVisible) {
        transformationMethod = PasswordTransformationMethod.getInstance()
    } else {
        transformationMethod = HideReturnsTransformationMethod.getInstance()
    }
    setSelection(text.length)
}