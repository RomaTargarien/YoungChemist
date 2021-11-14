package com.example.youngchemist.ui.base.validation

import android.content.Context
import android.util.Patterns
import com.example.youngchemist.R
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.Resource
import javax.inject.Inject

sealed class ValidationImpl : Validation {
    class LoginValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(login: String): Resource<String> {
            if (login.isEmpty()) {
                return Resource.Error(context.getString(R.string.error_input_empty))
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
                return Resource.Error(context.getString(R.string.error_not_a_valid_email))
            } else {
                return Resource.Success()
            }
        }
    }

    class PasswordValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(password: String): Resource<String> {
            if (password.isEmpty()) {
                return Resource.Error(context.getString(R.string.error_input_empty))
            }
            if (password.length < Constants.PASSWORD_MIN_LENGHT) {
                return Resource.Error(
                    context.getString(
                        R.string.error_password_too_short,
                        Constants.PASSWORD_MIN_LENGHT
                    )
                )
            } else {
                return Resource.Success()
            }
        }
    }

    class SurnameValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(surname: String): Resource<String> {
            if (surname.isEmpty()) {
                return Resource.Error(context.getString(R.string.error_input_empty))
            }
            if (surname.length < Constants.SURNAME_MIN_LENGHT) {
                return Resource.Error(
                    context.getString(
                        R.string.error_surname_too_short,
                        Constants.SURNAME_MIN_LENGHT
                    )
                )
            } else {
                return Resource.Success()
            }
        }
    }
}