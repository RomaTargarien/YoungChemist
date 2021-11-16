package com.example.youngchemist.ui.util

import android.content.Context
import android.util.Log
import com.example.youngchemist.ui.screen.auth.auth_error.AuthErrorHandler

inline fun safeCall(context: Context,action: () -> ResourceNetwork<String>): ResourceNetwork<String> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("TAG",e.javaClass.toString())
        AuthErrorHandler.handleException(context, e)
    }
}