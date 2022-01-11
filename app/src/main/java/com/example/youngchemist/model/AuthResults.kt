package com.example.youngchemist.model

import com.example.youngchemist.ui.util.Resource

data class AuthResults(
    var login: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var password: String? = null,
    var repeatedPassword: String? = null,
    var loginValidation: Resource<String>? = null,
    var nameValidation: Resource<String>?= null,
    val surnameValidation: Resource<String>? = null,
    val passwordValidation: Resource<String>? = null,
    val repeatedPasswordValidation: Resource<String>? = null
) {
    fun allSuccess(): Boolean {
        return (loginValidation is Resource.Success || loginValidation == null) &&
                (nameValidation is Resource.Success || nameValidation == null) &&
                (surnameValidation is Resource.Success || surnameValidation == null) &&
                (passwordValidation is Resource.Success || passwordValidation == null) &&
                (repeatedPasswordValidation is Resource.Success || repeatedPasswordValidation == null)
    }
    fun arePasswordEquals(): Boolean {
        return password.equals(repeatedPassword)
    }
}
