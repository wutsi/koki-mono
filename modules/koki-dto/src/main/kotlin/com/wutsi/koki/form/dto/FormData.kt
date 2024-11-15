package com.wutsi.koki.form.dto

import java.util.Date

data class FormData(
    val id: String = "",
    val formId: String = "",
    val status: FormDataStatus = FormDataStatus.UNKNOWN,
    val data: Map<String, Any> = emptyMap(),
    val workflowInstanceId: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
