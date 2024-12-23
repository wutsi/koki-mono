package com.wutsi.koki.form.event

data class FormSubmittedEvent(
    val tenantId: Long = -1,
    val formId: String = "",
    val formDataId: String = "",
    val activityInstanceId: String? = null,
    val userId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
