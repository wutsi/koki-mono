package com.wutsi.koki.form.dto

data class FormContent(
    val title: String = "",
    val description: String? = null,
    val elements: List<FormElement> = emptyList()
)
