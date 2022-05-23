package com.chemist.youngchemist.ui.util

import android.util.Log

inline fun <T> safeCall(action: () -> ResourceNetwork<T>): ResourceNetwork<T> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("TAG",e.localizedMessage)
        ResourceNetwork.Error(e.localizedMessage)
    }
}