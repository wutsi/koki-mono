package com.wutsi.koki.form.dto

data class ValidationRule(
    val min: String? = null,
    val max: String? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val errorText: String? = null,
)
