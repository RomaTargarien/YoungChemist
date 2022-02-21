package com.example.youngchemist.ui.base.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.youngchemist.ui.util.TextInputResource


@BindingAdapter("showErrorMessage")
fun TextView.showErrorMessage(state: TextInputResource<String>) {
    when (state) {
        is TextInputResource.InputInProcess -> {
            animate().alpha(0f).setDuration(300).start()
        }
        is TextInputResource.ErrorInput -> {
            state.message?.let { text = it }
            animate().alpha(1f).setDuration(300).start()
        }
        is TextInputResource.SuccessInput -> {
            animate().alpha(0f).setDuration(300).start()
        }
    }
}