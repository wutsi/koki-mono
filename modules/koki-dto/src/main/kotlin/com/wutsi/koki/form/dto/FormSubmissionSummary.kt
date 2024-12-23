package com.wutsi.koki.form.dto

import java.util.Date

data class FormSubmissionSummary(
    val id: String = "",
    val formId: String = "",
    val workflowInstanceId: String? = null,
    val activityInstanceId: String? = null,
    val submittedAt: Date = Date(),
    val submittedById: Long? = null,
)
