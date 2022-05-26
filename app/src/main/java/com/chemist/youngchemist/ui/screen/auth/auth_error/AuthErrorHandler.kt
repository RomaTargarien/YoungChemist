package com.chemist.youngchemist.ui.screen.auth.auth_error

import android.content.res.Resources
import com.chemist.youngchemist.R
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthErrorHandler {
    companion object {
        fun <T> handleException(resources: Resources, e: Exception): ResourceNetwork<T> {
            return when (e) {
                is FirebaseAuthUserCollisionException -> {
                    ResourceNetwork.Error(resources.getString(R.string.ERROR_EMAIL_ALREADY_IN_USE))
                }
                is FirebaseAuthInvalidUserException -> {
                    ResourceNetwork.Error(resources.getString(R.string.ERROR_USER_NOT_FOUND))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    ResourceNetwork.Error(resources.getString(R.string.ERROR_INVALID_CREDENTIALS))
                }
                else -> {
                    ResourceNetwork.Error(resources.getString(R.string.ERROR_UNKNOWN))
                }
            }
        }
    }
}