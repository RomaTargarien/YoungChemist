package com.example.youngchemist.ui.base.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.youngchemist.R

@BindingAdapter("passwordVisibility")
fun ImageView.setPasswordImage(isVisible: Boolean) {
    setImageResource(if (isVisible) R.drawable.ic_icon_view_password else R.drawable.ic_icon_hide_password)
}