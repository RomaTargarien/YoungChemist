package com.chemist.youngchemist.ui.util

import android.content.res.Resources
import android.util.Log
import com.chemist.youngchemist.ui.screen.auth.auth_error.AuthErrorHandler

inline fun <T> safeCall(action: () -> ResourceNetwork<T>): ResourceNetwork<T> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("TAG",e.localizedMessage)
        ResourceNetwork.Error(e.localizedMessage)
    }
}

inline fun <T> safeAuthCall(resources: Resources, action: () -> ResourceNetwork<T>): ResourceNetwork<T> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("TAG",e.localizedMessage)
        AuthErrorHandler.handleException(resources, e)
    }
}