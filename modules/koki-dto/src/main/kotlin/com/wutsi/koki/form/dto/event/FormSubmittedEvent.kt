package com.wutsi.koki.form.event

data class FormSubmittedEvent(
    val formId: String = "",
    val formDataId: String = "",
    val activityInstanceId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
