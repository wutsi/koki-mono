package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty

data class FormContent(
    @get:NotEmpty val name: String = "",
    val title: String = "",
    val description: String? = null,
    val elements: List<FormElement> = emptyList()
)
