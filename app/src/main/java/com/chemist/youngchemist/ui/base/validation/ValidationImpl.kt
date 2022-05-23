package com.chemist.youngchemist.ui.base.validation

import android.content.Context
import android.util.Patterns
import com.chemist.youngchemist.R
import com.chemist.youngchemist.ui.util.Constants
import com.chemist.youngchemist.ui.util.TextInputResource
import javax.inject.Inject

sealed class ValidationImpl : Validation {
    class LoginValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(login: String): TextInputResource<String> {
            if (login.isEmpty()) {
                return TextInputResource.ErrorInput(context.getString(R.string.error_input_empty))
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
                return TextInputResource.ErrorInput(context.getString(R.string.error_not_a_valid_email))
            } else {
                return TextInputResource.SuccessInput()
            }
        }
    }

    class PasswordValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(password: String): TextInputResource<String> {
            if (password.isEmpty()) {
                return TextInputResource.ErrorInput(context.getString(R.string.error_input_empty))
            }
            if (password.length < Constants.PASSWORD_MIN_LENGTH) {
                return TextInputResource.ErrorInput(
                    context.getString(
                        R.string.error_password_too_short,
                        Constants.PASSWORD_MIN_LENGTH
                    )
                )
            } else {
                return TextInputResource.SuccessInput()
            }
        }
    }

    class NameValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(name: String): TextInputResource<String> {
            if (name.isEmpty()) {
                return TextInputResource.ErrorInput(context.getString(R.string.error_input_empty))
            }
            if (name.length < Constants.NAME_MIN_LENGTH) {
                return TextInputResource.ErrorInput(
                    context.getString(
                        R.string.error_name_too_short,
                        Constants.NAME_MIN_LENGTH
                    )
                )
            } else {
                return TextInputResource.SuccessInput()
            }
        }
    }

    class SurnameValidation @Inject constructor(private val context: Context) : ValidationImpl() {
        override fun validate(surname: String): TextInputResource<String> {
            if (surname.isEmpty()) {
                return TextInputResource.ErrorInput(context.getString(R.string.error_input_empty))
            }
            if (surname.length < Constants.SURNAME_MIN_LENGTH) {
                return TextInputResource.ErrorInput(
                    context.getString(
                        R.string.error_surname_too_short,
                        Constants.SURNAME_MIN_LENGTH
                    )
                )
            } else {
                return TextInputResource.SuccessInput()
            }
        }
    }
}