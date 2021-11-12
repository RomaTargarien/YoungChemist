package com.example.youngchemist.ui.base.validation

import com.example.youngchemist.ui.util.Resource

interface Validation {
    fun validate(source: String): Resource<String>
}