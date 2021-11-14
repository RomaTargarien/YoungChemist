package com.example.youngchemist.model

import com.example.youngchemist.ui.util.Resource

data class AuthValidationResults(
    val login: Resource<String>? = null,
    val surname: Resource<String>? = null,
    val password: Resource<String>? = null,
    val repeatedPassword: Resource<String>? = null
)
