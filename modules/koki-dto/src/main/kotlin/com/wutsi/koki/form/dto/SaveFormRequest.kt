package com.wutsi.koki.form.dto

data class SaveFormRequest(
    val content: FormContent = FormContent(),
    val active: Boolean = true,
)
