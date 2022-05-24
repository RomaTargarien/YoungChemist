package com.chemist.youngchemist.ui.base.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.chemist.youngchemist.R
import com.squareup.picasso.Picasso

@BindingAdapter("passwordVisibility")
fun ImageView.setPasswordImage(isVisible: Boolean) {
    setImageResource(if (isVisible) R.drawable.ic_icon_view_password else R.drawable.ic_icon_hide_password)
}

@BindingAdapter("loadImageWithPlaceholder")
fun ImageView.loadImageWithPlaceholder(url: String) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_icon_happy_flask)
        .into(this)
}