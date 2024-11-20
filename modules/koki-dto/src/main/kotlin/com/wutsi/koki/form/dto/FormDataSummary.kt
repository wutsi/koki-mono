package com.wutsi.koki.form.dto

import java.util.Date

data class FormDataSummary(
    val id: String = "",
    val formId: String = "",
    val status: FormDataStatus = FormDataStatus.UNKNOWN,
    val workflowInstanceId: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
