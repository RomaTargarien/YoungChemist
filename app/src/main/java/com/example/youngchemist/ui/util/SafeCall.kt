package com.example.youngchemist.ui.util

import android.content.Context
import android.util.Log
import com.example.youngchemist.ui.screen.auth.auth_error.AuthErrorHandler

inline fun <T> safeCall(action: () -> ResourceNetwork<T>): ResourceNetwork<T> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("TAG",e.localizedMessage)
        ResourceNetwork.Error(e.localizedMessage)
    }
}