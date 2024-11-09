package com.wutsi.koki.form.dto

data class Form(
    val id: Long = -1,
    val content: FormContent = FormContent()
)
