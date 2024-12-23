package com.wutsi.koki.form.dto

import java.util.Date

data class FormSubmission(
    val id: String = "",
    val formId: String = "",
    val data: Map<String, Any> = emptyMap(),
    val workflowInstanceId: String? = null,
    val activityInstanceId: String? = null,
    val submittedAt: Date = Date(),
    val submittedById: Long? = null,
)
