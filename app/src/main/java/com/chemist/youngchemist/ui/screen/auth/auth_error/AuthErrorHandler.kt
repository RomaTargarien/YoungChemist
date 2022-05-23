package com.chemist.youngchemist.ui.screen.auth.auth_error

import android.content.Context
import com.chemist.youngchemist.R
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthErrorHandler {
    companion object {
        fun handleException(context: Context, e: Exception): ResourceNetwork<String> {
            when (e) {
                is FirebaseAuthUserCollisionException -> {
                    return ResourceNetwork.Error(context.getString(R.string.ERROR_EMAIL_ALREADY_IN_USE))
                }
                is FirebaseAuthInvalidUserException -> {
                    return ResourceNetwork.Error(context.getString(R.string.ERROR_USER_NOT_FOUND))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    return ResourceNetwork.Error(context.getString(R.string.ERROR_INVALID_CREDENTIALS))
                }
                else -> {
                    return ResourceNetwork.Error(context.getString(R.string.ERROR_UNKNOWN))
                }
            }
        }
    }
}