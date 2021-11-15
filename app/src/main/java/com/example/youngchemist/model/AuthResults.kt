package com.example.youngchemist.model

import com.example.youngchemist.ui.util.Resource

data class AuthResults(
    val login: Resource<String>? = null,
    val surname: Resource<String>? = null,
    val password: Resource<String>? = null,
    val repeatedPassword: Resource<String>? = null
) {
    fun allSuccess(): Boolean {
        return login is Resource.Success &&
                surname is Resource.Success &&
                password is Resource.Success &&
                repeatedPassword is Resource.Success
    }
}
