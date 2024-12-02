package com.wutsi.koki.form.dto

import java.util.Date

data class Form(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val active: Boolean = true,
    val content: FormContent = FormContent(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
