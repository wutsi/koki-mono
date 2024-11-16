package com.wutsi.koki.form.event

data class FormUpdatedEvent(
    val tenantId: Long = -1,
    val formId: String = "",
    val formDataId: String = "",
    val activityInstanceId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
