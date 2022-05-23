package com.chemist.youngchemist.ui.base.validation

import com.chemist.youngchemist.ui.util.TextInputResource

interface Validation {
    fun validate(source: String): TextInputResource<String>
}