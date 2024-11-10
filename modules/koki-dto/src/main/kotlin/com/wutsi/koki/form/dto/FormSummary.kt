package com.wutsi.koki.form.dto

import java.util.Date

data class FormSummary(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
