package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty

data class ImportFormRequest(
    @NotEmpty() val title: String = "",
    val description: String? = null,
    val content: FormContent = FormContent()
)
