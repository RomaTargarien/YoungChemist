package com.chemist.youngchemist.ui.util

import android.util.Log
import java.lang.IllegalArgumentException
import javax.annotation.Nullable

class Event<T>(content: T) {

    private var mContent: T
    private var hasBeenHandled = false

    init {
        if (content == null) {
            throw IllegalArgumentException("Null values are not allowed")
        }
        mContent = content
    }

    @Nullable
    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) {
            return null
        } else {
            hasBeenHandled = true
            return mContent
        }
    }

    fun hasBeenHandled() = hasBeenHandled
}