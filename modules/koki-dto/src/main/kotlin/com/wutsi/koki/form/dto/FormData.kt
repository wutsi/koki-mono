package com.wutsi.koki.form.dto

import java.util.Date

data class FormData(
    val id: String = "",
    val formId: String = "",
    val data: Map<String, String> = emptyMap(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
