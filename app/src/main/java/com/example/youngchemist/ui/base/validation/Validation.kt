package com.example.youngchemist.ui.base.validation

import com.example.youngchemist.ui.util.TextInputResource

interface Validation {
    fun validate(source: String): TextInputResource<String>
}