package com.example.youngchemist.ui.base.validation

import android.content.Context
import android.util.Patterns
import com.example.youngchemist.R
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.Resource
import javax.inject.Inject

sealed class ValidationImpl : Validation {
    class EmailValidation @Inject constructor(private val context: Context): ValidationImpl() {
        override fun validate(email: String): Resource<String> {
            if (email.isEmpty()) {
                return Resource.Error(context.getString(R.string.error_input_empty))
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Resource.Error(context.getString(R.string.error_not_a_valid_email))
            } else {
                return Resource.Success("")
            }
        }
    }

    class PasswordValidation @Inject constructor(private val context: Context): ValidationImpl() {
        override fun validate(password: String): Resource<String> {
            if (password.isEmpty()) {
                return Resource.Error(context.getString(R.string.error_input_empty))
            }
            if (password.length < Constants.PASSWORD_MIN_LENGHT) {
                return Resource.Error(context.getString(R.string.error_password_too_short,
                    Constants.PASSWORD_MIN_LENGHT
                ))
            } else {
                return Resource.Success("")
            }
        }
    }
}